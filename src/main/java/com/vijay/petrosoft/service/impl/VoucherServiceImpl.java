package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Account;
import com.vijay.petrosoft.domain.Voucher;
import com.vijay.petrosoft.domain.VoucherEntry;
import com.vijay.petrosoft.dto.VoucherDTO;
import com.vijay.petrosoft.dto.VoucherEntryDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.vijay.petrosoft.repository.AccountRepository;
import com.vijay.petrosoft.repository.VoucherRepository;
import com.vijay.petrosoft.repository.VoucherEntryRepository;
import com.vijay.petrosoft.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherEntryRepository voucherEntryRepository;
    private final AccountRepository accountRepository;

    @Override
    public VoucherDTO createVoucher(VoucherDTO voucherDTO) {
        // Check if voucher number already exists
        if (voucherRepository.existsByVoucherNumberAndPumpId(voucherDTO.getVoucherNumber(), voucherDTO.getPumpId())) {
            throw new DuplicateResourceException("Voucher number already exists: " + voucherDTO.getVoucherNumber());
        }

        // Validate double entry
        if (!validateDoubleEntry(voucherDTO)) {
            throw new IllegalArgumentException("Invalid double entry: Debit and Credit amounts must be equal");
        }

        // Generate voucher number if not provided
        if (voucherDTO.getVoucherNumber() == null || voucherDTO.getVoucherNumber().isEmpty()) {
            voucherDTO.setVoucherNumber(generateVoucherNumber(voucherDTO.getVoucherType(), voucherDTO.getPumpId()));
        }

        // Create voucher
        Voucher voucher = Voucher.builder()
                .voucherNumber(voucherDTO.getVoucherNumber())
                .voucherType(voucherDTO.getVoucherType())
                .voucherDate(voucherDTO.getVoucherDate())
                .narration(voucherDTO.getNarration())
                .reference(voucherDTO.getReference())
                .totalAmount(voucherDTO.getTotalAmount())
                .pumpId(voucherDTO.getPumpId())
                .userId(voucherDTO.getUserId())
                .status(voucherDTO.getStatus())
                .isPosted(voucherDTO.isPosted())
                .isCancelled(voucherDTO.isCancelled())
                .partyName(voucherDTO.getPartyName())
                .partyAccountCode(voucherDTO.getPartyAccountCode())
                .chequeNumber(voucherDTO.getChequeNumber())
                .chequeDate(voucherDTO.getChequeDate())
                .bankName(voucherDTO.getBankName())
                .paymentMode(voucherDTO.getPaymentMode())
                .isReconciled(voucherDTO.isReconciled())
                .additionalInfo(voucherDTO.getAdditionalInfo() != null ? 
                    voucherDTO.getAdditionalInfo().toString() : null)
                .build();

        Voucher savedVoucher = voucherRepository.save(voucher);

        // Create voucher entries
        List<VoucherEntry> voucherEntries = new ArrayList<>();
        for (VoucherEntryDTO entryDTO : voucherDTO.getVoucherEntries()) {
            Account account = accountRepository.findById(entryDTO.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + entryDTO.getAccountId()));

            VoucherEntry entry = VoucherEntry.builder()
                    .voucher(savedVoucher)
                    .account(account)
                    .narration(entryDTO.getNarration())
                    .entryType(entryDTO.getEntryType())
                    .amount(entryDTO.getAmount())
                    .debitAmount(entryDTO.getDebitAmount())
                    .creditAmount(entryDTO.getCreditAmount())
                    .reference(entryDTO.getReference())
                    .partyName(entryDTO.getPartyName())
                    .additionalInfo(entryDTO.getAdditionalInfo() != null ? 
                        entryDTO.getAdditionalInfo().toString() : null)
                    .build();

            voucherEntries.add(voucherEntryRepository.save(entry));
        }

        savedVoucher.setVoucherEntries(voucherEntries);
        log.info("Voucher created with number: {}", voucherDTO.getVoucherNumber());
        
        return convertToDTO(savedVoucher);
    }

    @Override
    public VoucherDTO updateVoucher(Long id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (voucher.isPosted()) {
            throw new IllegalStateException("Cannot update posted voucher");
        }

        if (voucher.isCancelled()) {
            throw new IllegalStateException("Cannot update cancelled voucher");
        }

        // Validate double entry
        if (!validateDoubleEntry(voucherDTO)) {
            throw new IllegalArgumentException("Invalid double entry: Debit and Credit amounts must be equal");
        }

        // Update voucher fields
        voucher.setVoucherType(voucherDTO.getVoucherType());
        voucher.setVoucherDate(voucherDTO.getVoucherDate());
        voucher.setNarration(voucherDTO.getNarration());
        voucher.setReference(voucherDTO.getReference());
        voucher.setTotalAmount(voucherDTO.getTotalAmount());
        voucher.setPartyName(voucherDTO.getPartyName());
        voucher.setPartyAccountCode(voucherDTO.getPartyAccountCode());
        voucher.setChequeNumber(voucherDTO.getChequeNumber());
        voucher.setChequeDate(voucherDTO.getChequeDate());
        voucher.setBankName(voucherDTO.getBankName());
        voucher.setPaymentMode(voucherDTO.getPaymentMode());
        voucher.setAdditionalInfo(voucherDTO.getAdditionalInfo() != null ? 
            voucherDTO.getAdditionalInfo().toString() : null);

        // Update voucher entries
        voucherEntryRepository.deleteAll(voucherEntryRepository.findByVoucherId(id));
        
        List<VoucherEntry> voucherEntries = new ArrayList<>();
        for (VoucherEntryDTO entryDTO : voucherDTO.getVoucherEntries()) {
            Account account = accountRepository.findById(entryDTO.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + entryDTO.getAccountId()));

            VoucherEntry entry = VoucherEntry.builder()
                    .voucher(voucher)
                    .account(account)
                    .narration(entryDTO.getNarration())
                    .entryType(entryDTO.getEntryType())
                    .amount(entryDTO.getAmount())
                    .debitAmount(entryDTO.getDebitAmount())
                    .creditAmount(entryDTO.getCreditAmount())
                    .reference(entryDTO.getReference())
                    .partyName(entryDTO.getPartyName())
                    .additionalInfo(entryDTO.getAdditionalInfo() != null ? 
                        entryDTO.getAdditionalInfo().toString() : null)
                    .build();

            voucherEntries.add(voucherEntryRepository.save(entry));
        }

        voucher.setVoucherEntries(voucherEntries);
        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher updated for ID: {}", id);
        
        return convertToDTO(updatedVoucher);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoucherDTO> getVoucherById(Long id) {
        return voucherRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoucherDTO> getVoucherByNumber(String voucherNumber) {
        return voucherRepository.findByVoucherNumber(voucherNumber)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByPumpId(Long pumpId) {
        return voucherRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (voucher.isPosted()) {
            throw new IllegalStateException("Cannot delete posted voucher");
        }

        voucherEntryRepository.deleteAll(voucherEntryRepository.findByVoucherId(id));
        voucherRepository.deleteById(id);
        log.info("Voucher deleted for ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByType(Voucher.VoucherType voucherType) {
        return voucherRepository.findByPumpIdAndVoucherType(null, voucherType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByPumpIdAndType(Long pumpId, Voucher.VoucherType voucherType) {
        return voucherRepository.findByPumpIdAndVoucherType(pumpId, voucherType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByStatus(Voucher.VoucherStatus status) {
        return voucherRepository.findByPumpIdAndStatus(null, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByPumpIdAndStatus(Long pumpId, Voucher.VoucherStatus status) {
        return voucherRepository.findByPumpIdAndStatus(pumpId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO approveVoucher(Long id, Long approvedBy) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (voucher.isPosted()) {
            throw new IllegalStateException("Cannot approve posted voucher");
        }

        if (voucher.isCancelled()) {
            throw new IllegalStateException("Cannot approve cancelled voucher");
        }

        voucher.setStatus(Voucher.VoucherStatus.APPROVED);
        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher approved for ID: {}", id);
        
        return convertToDTO(updatedVoucher);
    }

    @Override
    public VoucherDTO postVoucher(Long id, Long postedBy) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (voucher.isPosted()) {
            throw new IllegalStateException("Voucher is already posted");
        }

        if (voucher.isCancelled()) {
            throw new IllegalStateException("Cannot post cancelled voucher");
        }

        voucher.setStatus(Voucher.VoucherStatus.POSTED);
        voucher.setPosted(true);
        voucher.setPostedAt(LocalDateTime.now());
        voucher.setPostedBy(postedBy);

        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher posted for ID: {}", id);
        
        return convertToDTO(updatedVoucher);
    }

    @Override
    public VoucherDTO cancelVoucher(Long id, Long cancelledBy, String reason) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (voucher.isCancelled()) {
            throw new IllegalStateException("Voucher is already cancelled");
        }

        voucher.setStatus(Voucher.VoucherStatus.CANCELLED);
        voucher.setCancelled(true);
        voucher.setCancelledAt(LocalDateTime.now());
        voucher.setCancelledBy(cancelledBy);
        voucher.setCancellationReason(reason);

        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher cancelled for ID: {}", id);
        
        return convertToDTO(updatedVoucher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate) {
        return voucherRepository.findByPumpIdAndVoucherDateBetween(pumpId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByTypeAndDateRange(Long pumpId, Voucher.VoucherType voucherType, LocalDate startDate, LocalDate endDate) {
        return voucherRepository.findByPumpIdAndVoucherTypeAndVoucherDateBetween(pumpId, voucherType, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getPostedVouchers(Long pumpId) {
        return voucherRepository.findPostedVouchersByPumpIdOrderByDateDesc(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getPostedVouchersByType(Long pumpId, Voucher.VoucherType voucherType) {
        return voucherRepository.findPostedVouchersByPumpIdAndTypeOrderByDateDesc(pumpId, voucherType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getUnpostedVouchers(Long pumpId) {
        return voucherRepository.findByPumpId(pumpId).stream()
                .filter(v -> !v.isPosted())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getCancelledVouchers(Long pumpId) {
        return voucherRepository.findByPumpIdAndIsCancelledTrue(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO uncancelVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (!voucher.isCancelled()) {
            throw new IllegalStateException("Voucher is not cancelled");
        }

        voucher.setStatus(Voucher.VoucherStatus.DRAFT);
        voucher.setCancelled(false);
        voucher.setCancelledAt(null);
        voucher.setCancelledBy(null);
        voucher.setCancellationReason(null);

        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher uncancelled for ID: {}", id);
        
        return convertToDTO(updatedVoucher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByUser(Long pumpId, Long userId) {
        return voucherRepository.findByPumpIdAndUserId(pumpId, userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByPartyName(Long pumpId, String partyName) {
        return voucherRepository.findByPumpIdAndPartyNameContaining(pumpId, partyName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByPaymentMode(Long pumpId, String paymentMode) {
        return voucherRepository.findVouchersByPumpIdAndPaymentModeAndDateRange(pumpId, paymentMode, LocalDate.MIN, LocalDate.MAX).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByPaymentModeAndDateRange(Long pumpId, String paymentMode, LocalDate startDate, LocalDate endDate) {
        return voucherRepository.findVouchersByPumpIdAndPaymentModeAndDateRange(pumpId, paymentMode, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByChequeNumber(Long pumpId, String chequeNumber) {
        return voucherRepository.findVouchersByPumpIdAndChequeNumber(pumpId, chequeNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVouchersByChequeDate(Long pumpId, LocalDate chequeDate) {
        // This would need a custom query in the repository
        return getVouchersByPumpId(pumpId).stream()
                .filter(v -> v.getChequeDate() != null && v.getChequeDate().equals(chequeDate))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getUnreconciledVouchers(Long pumpId) {
        return voucherRepository.findUnreconciledVouchersByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getReconciledVouchers(Long pumpId) {
        return getVouchersByPumpId(pumpId).stream()
                .filter(VoucherDTO::isReconciled)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO reconcileVoucher(Long id, Long reconciledBy) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (!voucher.isPosted()) {
            throw new IllegalStateException("Cannot reconcile unposted voucher");
        }

        voucher.setReconciled(true);
        voucher.setReconciledAt(LocalDateTime.now());
        voucher.setReconciledBy(reconciledBy);

        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher reconciled for ID: {}", id);
        
        return convertToDTO(updatedVoucher);
    }

    @Override
    public VoucherDTO unreconcileVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        voucher.setReconciled(false);
        voucher.setReconciledAt(null);
        voucher.setReconciledBy(null);

        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher unreconciled for ID: {}", id);
        
        return convertToDTO(updatedVoucher);
    }

    @Override
    public String generateVoucherNumber(Voucher.VoucherType voucherType, Long pumpId) {
        String prefix = voucherType.name().substring(0, 3).toUpperCase();
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = voucherRepository.countPostedVouchersByPumpId(pumpId) + 1;
        return String.format("%s%s%04d", prefix, dateStr, sequence);
    }

    @Override
    public boolean validateVoucherNumber(String voucherNumber) {
        return voucherNumber != null && voucherNumber.matches("^[A-Z]{3}\\d{8}\\d{4}$");
    }

    @Override
    public boolean validateDoubleEntry(VoucherDTO voucherDTO) {
        BigDecimal totalDebit = calculateTotalDebit(voucherDTO);
        BigDecimal totalCredit = calculateTotalCredit(voucherDTO);
        return totalDebit.compareTo(totalCredit) == 0;
    }

    @Override
    public boolean validateVoucherBalances(VoucherDTO voucherDTO) {
        return validateDoubleEntry(voucherDTO) && 
               voucherDTO.getTotalAmount().compareTo(calculateTotalDebit(voucherDTO)) == 0;
    }

    @Override
    public BigDecimal calculateTotalDebit(VoucherDTO voucherDTO) {
        return voucherDTO.getVoucherEntries().stream()
                .map(VoucherEntryDTO::getDebitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalCredit(VoucherDTO voucherDTO) {
        return voucherDTO.getVoucherEntries().stream()
                .map(VoucherEntryDTO::getCreditAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByType(Long pumpId, Voucher.VoucherType voucherType, LocalDate startDate, LocalDate endDate) {
        Double total = voucherRepository.sumTotalAmountByPumpIdAndTypeAndDateRange(pumpId, voucherType, startDate, endDate);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> searchVouchersByNumber(Long pumpId, String voucherNumber) {
        return voucherRepository.findByPumpIdAndVoucherNumberContaining(pumpId, voucherNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> searchVouchersByReference(Long pumpId, String reference) {
        return voucherRepository.findByPumpIdAndReferenceContaining(pumpId, reference).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVoucherAnalytics(Long pumpId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalVouchers", voucherRepository.countPostedVouchersByPumpId(pumpId));
        analytics.put("unpostedVouchers", getUnpostedVouchers(pumpId).size());
        analytics.put("cancelledVouchers", getCancelledVouchers(pumpId).size());
        analytics.put("unreconciledVouchers", getUnreconciledVouchers(pumpId).size());
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getVoucherCountByType(Long pumpId) {
        Map<String, Long> counts = new HashMap<>();
        for (Voucher.VoucherType type : Voucher.VoucherType.values()) {
            counts.put(type.name(), voucherRepository.countPostedVouchersByPumpIdAndType(pumpId, type));
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getVoucherAmountByType(Long pumpId, LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> amounts = new HashMap<>();
        for (Voucher.VoucherType type : Voucher.VoucherType.values()) {
            amounts.put(type.name(), getTotalAmountByType(pumpId, type, startDate, endDate));
        }
        return amounts;
    }

    @Override
    public List<VoucherDTO> bulkPostVouchers(List<Long> voucherIds, Long postedBy) {
        List<VoucherDTO> postedVouchers = new ArrayList<>();
        
        for (Long voucherId : voucherIds) {
            try {
                VoucherDTO postedVoucher = postVoucher(voucherId, postedBy);
                postedVouchers.add(postedVoucher);
            } catch (Exception e) {
                log.error("Failed to post voucher ID: {}", voucherId, e);
            }
        }
        
        return postedVouchers;
    }

    @Override
    public List<VoucherDTO> bulkCancelVouchers(List<Long> voucherIds, Long cancelledBy, String reason) {
        List<VoucherDTO> cancelledVouchers = new ArrayList<>();
        
        for (Long voucherId : voucherIds) {
            try {
                VoucherDTO cancelledVoucher = cancelVoucher(voucherId, cancelledBy, reason);
                cancelledVouchers.add(cancelledVoucher);
            } catch (Exception e) {
                log.error("Failed to cancel voucher ID: {}", voucherId, e);
            }
        }
        
        return cancelledVouchers;
    }

    // Specific Voucher Type Methods
    @Override
    public VoucherDTO createCustomerReceipt(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.CUSTOMER_RECEIPT);
        return createVoucher(voucherDTO);
    }

    @Override
    public VoucherDTO createPaymentVoucher(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.PAYMENT_VOUCHER);
        return createVoucher(voucherDTO);
    }

    @Override
    public VoucherDTO createMiscellaneousReceipt(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.MISCELLANEOUS_RECEIPT);
        return createVoucher(voucherDTO);
    }

    @Override
    public VoucherDTO createJournalVoucher(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.JOURNAL_VOUCHER);
        return createVoucher(voucherDTO);
    }

    @Override
    public VoucherDTO createCashDeposit(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.CASH_DEPOSIT);
        return createVoucher(voucherDTO);
    }

    @Override
    public VoucherDTO createCashWithdrawal(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.CASH_WITHDRAWAL);
        return createVoucher(voucherDTO);
    }

    @Override
    public VoucherDTO createContraVoucher(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.CONTRA_VOUCHER);
        return createVoucher(voucherDTO);
    }

    @Override
    public VoucherDTO createChequeReturn(VoucherDTO voucherDTO) {
        voucherDTO.setVoucherType(Voucher.VoucherType.CHEQUE_RETURN);
        return createVoucher(voucherDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getVoucherTemplates(Voucher.VoucherType voucherType) {
        // This would typically load from a template configuration
        // For now, return empty list
        return new ArrayList<>();
    }

    @Override
    public VoucherDTO createVoucherFromTemplate(Long templateId, Long pumpId, Long userId) {
        // This would typically load from a template and create a new voucher
        // For now, throw not implemented
        throw new UnsupportedOperationException("Voucher templates not implemented yet");
    }

    private VoucherDTO convertToDTO(Voucher voucher) {
        return VoucherDTO.builder()
                .id(voucher.getId())
                .voucherNumber(voucher.getVoucherNumber())
                .voucherType(voucher.getVoucherType())
                .voucherDate(voucher.getVoucherDate())
                .narration(voucher.getNarration())
                .reference(voucher.getReference())
                .totalAmount(voucher.getTotalAmount())
                .pumpId(voucher.getPumpId())
                .userId(voucher.getUserId())
                .status(voucher.getStatus())
                .isPosted(voucher.isPosted())
                .postedAt(voucher.getPostedAt())
                .postedBy(voucher.getPostedBy())
                .isCancelled(voucher.isCancelled())
                .cancelledAt(voucher.getCancelledAt())
                .cancelledBy(voucher.getCancelledBy())
                .cancellationReason(voucher.getCancellationReason())
                .partyName(voucher.getPartyName())
                .partyAccountCode(voucher.getPartyAccountCode())
                .chequeNumber(voucher.getChequeNumber())
                .chequeDate(voucher.getChequeDate())
                .bankName(voucher.getBankName())
                .paymentMode(voucher.getPaymentMode())
                .isReconciled(voucher.isReconciled())
                .reconciledAt(voucher.getReconciledAt())
                .reconciledBy(voucher.getReconciledBy())
                .voucherEntries(voucher.getVoucherEntries().stream()
                        .map(this::convertEntryToDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private VoucherEntryDTO convertEntryToDTO(VoucherEntry entry) {
        return VoucherEntryDTO.builder()
                .id(entry.getId())
                .accountId(entry.getAccount().getId())
                .narration(entry.getNarration())
                .entryType(entry.getEntryType())
                .amount(entry.getAmount())
                .debitAmount(entry.getDebitAmount())
                .creditAmount(entry.getCreditAmount())
                .reference(entry.getReference())
                .partyName(entry.getPartyName())
                .build();
    }
}
