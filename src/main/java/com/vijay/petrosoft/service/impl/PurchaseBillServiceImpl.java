package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.PurchaseBill;
import com.vijay.petrosoft.domain.PurchaseBillItem;
import com.vijay.petrosoft.domain.Supplier;
import com.vijay.petrosoft.dto.PurchaseBillDTO;
import com.vijay.petrosoft.dto.PurchaseBillItemDTO;
import com.vijay.petrosoft.exception.ResourceNotFoundException;
import com.vijay.petrosoft.exception.BusinessLogicException;
import com.vijay.petrosoft.repository.PurchaseBillRepository;
import com.vijay.petrosoft.repository.PurchaseBillItemRepository;
import com.vijay.petrosoft.repository.SupplierRepository;
import com.vijay.petrosoft.service.PurchaseBillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PurchaseBillServiceImpl implements PurchaseBillService {
    
    private final PurchaseBillRepository purchaseBillRepository;
    private final PurchaseBillItemRepository purchaseBillItemRepository;
    private final SupplierRepository supplierRepository;
    
    @Override
    public PurchaseBillDTO createPurchaseBill(PurchaseBillDTO purchaseBillDTO) {
        log.info("Creating purchase bill: {}", purchaseBillDTO.getBillNumber());
        
        // Validate bill number uniqueness
        if (!isBillNumberUnique(purchaseBillDTO.getBillNumber(), null)) {
            throw new RuntimeException("Bill number already exists: " + purchaseBillDTO.getBillNumber());
        }
        
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(purchaseBillDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + purchaseBillDTO.getSupplierId()));
        
        PurchaseBill purchaseBill = convertToEntity(purchaseBillDTO);
        purchaseBill.setSupplier(supplier);
        purchaseBill.setCreatedAt(LocalDateTime.now());
        
        // Calculate totals
        calculateBillTotals(purchaseBill);
        
        PurchaseBill savedBill = purchaseBillRepository.save(purchaseBill);
        
        // Save items
        if (purchaseBillDTO.getItems() != null && !purchaseBillDTO.getItems().isEmpty()) {
            for (PurchaseBillItemDTO itemDTO : purchaseBillDTO.getItems()) {
                PurchaseBillItem item = convertToItemEntity(itemDTO);
                item.setPurchaseBill(savedBill);
                item.calculateTotals();
                purchaseBillItemRepository.save(item);
            }
        }
        
        log.info("Purchase bill created successfully with ID: {}", savedBill.getId());
        return convertToDTO(savedBill);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PurchaseBillDTO getPurchaseBillById(Long id) {
        log.info("Getting purchase bill by ID: {}", id);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        return convertToDTO(purchaseBill);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PurchaseBillDTO getPurchaseBillByBillNumber(String billNumber) {
        log.info("Getting purchase bill by bill number: {}", billNumber);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with bill number: " + billNumber));
        
        return convertToDTO(purchaseBill);
    }
    
    @Override
    public PurchaseBillDTO updatePurchaseBill(Long id, PurchaseBillDTO purchaseBillDTO) {
        log.info("Updating purchase bill with ID: {}", id);
        
        PurchaseBill existingBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        // Check if bill can be updated
        if (!canUpdateBill(existingBill)) {
            throw new BusinessLogicException("Cannot update bill in status: " + existingBill.getStatus());
        }
        
        // Validate bill number uniqueness if changed
        if (!purchaseBillDTO.getBillNumber().equals(existingBill.getBillNumber()) && 
            !isBillNumberUnique(purchaseBillDTO.getBillNumber(), id)) {
            throw new RuntimeException("Bill number already exists: " + purchaseBillDTO.getBillNumber());
        }
        
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(purchaseBillDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + purchaseBillDTO.getSupplierId()));
        
        // Update fields
        updateBillFields(existingBill, purchaseBillDTO);
        existingBill.setSupplier(supplier);
        existingBill.setUpdatedAt(LocalDateTime.now());
        
        // Delete existing items and save new ones
        purchaseBillItemRepository.deleteByPurchaseBillId(id);
        
        // Save new items
        if (purchaseBillDTO.getItems() != null && !purchaseBillDTO.getItems().isEmpty()) {
            for (PurchaseBillItemDTO itemDTO : purchaseBillDTO.getItems()) {
                PurchaseBillItem item = convertToItemEntity(itemDTO);
                item.setPurchaseBill(existingBill);
                item.calculateTotals();
                purchaseBillItemRepository.save(item);
            }
        }
        
        // Recalculate totals
        calculateBillTotals(existingBill);
        
        PurchaseBill updatedBill = purchaseBillRepository.save(existingBill);
        log.info("Purchase bill updated successfully with ID: {}", updatedBill.getId());
        
        return convertToDTO(updatedBill);
    }
    
    @Override
    public void deletePurchaseBill(Long id) {
        log.info("Deleting purchase bill with ID: {}", id);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        // Check if bill can be deleted
        if (!canDeleteBill(purchaseBill)) {
            throw new BusinessLogicException("Cannot delete bill in status: " + purchaseBill.getStatus());
        }
        
        purchaseBillRepository.deleteById(id);
        log.info("Purchase bill deleted successfully with ID: {}", id);
    }
    
    @Override
    public PurchaseBillDTO submitPurchaseBill(Long id) {
        log.info("Submitting purchase bill with ID: {}", id);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        if (!PurchaseBill.Status.DRAFT.equals(purchaseBill.getStatus())) {
            throw new BusinessLogicException("Only draft bills can be submitted");
        }
        
        purchaseBill.setStatus(PurchaseBill.Status.SUBMITTED);
        purchaseBill.setUpdatedAt(LocalDateTime.now());
        
        PurchaseBill updatedBill = purchaseBillRepository.save(purchaseBill);
        log.info("Purchase bill submitted successfully with ID: {}", updatedBill.getId());
        
        return convertToDTO(updatedBill);
    }
    
    @Override
    public PurchaseBillDTO approvePurchaseBill(Long id, Long approvedBy) {
        log.info("Approving purchase bill with ID: {} by user: {}", id, approvedBy);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        if (!canApproveBill(id)) {
            throw new BusinessLogicException("Cannot approve bill in current status: " + purchaseBill.getStatus());
        }
        
        purchaseBill.setStatus(PurchaseBill.Status.APPROVED);
        purchaseBill.setApprovedBy(approvedBy);
        purchaseBill.setApprovedAt(LocalDateTime.now());
        purchaseBill.setUpdatedAt(LocalDateTime.now());
        
        PurchaseBill updatedBill = purchaseBillRepository.save(purchaseBill);
        log.info("Purchase bill approved successfully with ID: {}", updatedBill.getId());
        
        return convertToDTO(updatedBill);
    }
    
    @Override
    public PurchaseBillDTO receivePurchaseBill(Long id, Long receivedBy) {
        log.info("Receiving purchase bill with ID: {} by user: {}", id, receivedBy);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        if (!canReceiveBill(id)) {
            throw new BusinessLogicException("Cannot receive bill in current status: " + purchaseBill.getStatus());
        }
        
        purchaseBill.setStatus(PurchaseBill.Status.RECEIVED);
        purchaseBill.setUpdatedAt(LocalDateTime.now());
        
        PurchaseBill updatedBill = purchaseBillRepository.save(purchaseBill);
        log.info("Purchase bill received successfully with ID: {}", updatedBill.getId());
        
        return convertToDTO(updatedBill);
    }
    
    @Override
    public PurchaseBillDTO invoicePurchaseBill(Long id) {
        log.info("Invoicing purchase bill with ID: {}", id);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        if (!PurchaseBill.Status.RECEIVED.equals(purchaseBill.getStatus())) {
            throw new BusinessLogicException("Only received bills can be invoiced");
        }
        
        purchaseBill.setStatus(PurchaseBill.Status.INVOICED);
        purchaseBill.setUpdatedAt(LocalDateTime.now());
        
        PurchaseBill updatedBill = purchaseBillRepository.save(purchaseBill);
        log.info("Purchase bill invoiced successfully with ID: {}", updatedBill.getId());
        
        return convertToDTO(updatedBill);
    }
    
    @Override
    public PurchaseBillDTO payPurchaseBill(Long id, Long paidBy) {
        log.info("Paying purchase bill with ID: {} by user: {}", id, paidBy);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        if (!canPayBill(id)) {
            throw new BusinessLogicException("Cannot pay bill in current status: " + purchaseBill.getStatus());
        }
        
        purchaseBill.setStatus(PurchaseBill.Status.PAID);
        purchaseBill.setPaymentStatus(PurchaseBill.PaymentStatus.PAID);
        purchaseBill.setUpdatedAt(LocalDateTime.now());
        
        PurchaseBill updatedBill = purchaseBillRepository.save(purchaseBill);
        log.info("Purchase bill paid successfully with ID: {}", updatedBill.getId());
        
        return convertToDTO(updatedBill);
    }
    
    @Override
    public PurchaseBillDTO cancelPurchaseBill(Long id, String reason) {
        log.info("Cancelling purchase bill with ID: {} for reason: {}", id, reason);
        
        PurchaseBill purchaseBill = purchaseBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + id));
        
        if (!canCancelBill(id)) {
            throw new BusinessLogicException("Cannot cancel bill in current status: " + purchaseBill.getStatus());
        }
        
        purchaseBill.setStatus(PurchaseBill.Status.CANCELLED);
        purchaseBill.setPaymentStatus(PurchaseBill.PaymentStatus.CANCELLED);
        purchaseBill.setNotes(purchaseBill.getNotes() + "\nCancelled: " + reason);
        purchaseBill.setUpdatedAt(LocalDateTime.now());
        
        PurchaseBill updatedBill = purchaseBillRepository.save(purchaseBill);
        log.info("Purchase bill cancelled successfully with ID: {}", updatedBill.getId());
        
        return convertToDTO(updatedBill);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getPurchaseBillsByPump(Long pumpId) {
        log.info("Getting purchase bills by pump ID: {}", pumpId);
        
        List<PurchaseBill> bills = purchaseBillRepository.findByPumpIdOrderByBillDateDesc(pumpId);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseBillDTO> getPurchaseBillsByPump(Long pumpId, Pageable pageable) {
        log.info("Getting purchase bills by pump ID: {} with pagination", pumpId);
        
        Page<PurchaseBill> bills = purchaseBillRepository.findByPumpIdOrderByBillDateDesc(pumpId, pageable);
        return bills.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getPurchaseBillsBySupplier(Long supplierId) {
        log.info("Getting purchase bills by supplier ID: {}", supplierId);
        
        List<PurchaseBill> bills = purchaseBillRepository.findBySupplierIdOrderByBillDateDesc(supplierId);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getPurchaseBillsByStatus(PurchaseBill.Status status) {
        log.info("Getting purchase bills by status: {}", status);
        
        List<PurchaseBill> bills = purchaseBillRepository.findByStatusOrderByBillDateDesc(status);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getPurchaseBillsByPaymentStatus(PurchaseBill.PaymentStatus paymentStatus) {
        log.info("Getting purchase bills by payment status: {}", paymentStatus);
        
        List<PurchaseBill> bills = purchaseBillRepository.findByPaymentStatusOrderByBillDateDesc(paymentStatus);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getPurchaseBillsByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting purchase bills by date range: {} to {} for pump: {}", startDate, endDate, pumpId);
        
        List<PurchaseBill> bills = purchaseBillRepository.findByBillDateBetweenAndPumpIdOrderByBillDateDesc(startDate, endDate, pumpId);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getPurchaseBillsByBillType(PurchaseBill.BillType billType) {
        log.info("Getting purchase bills by bill type: {}", billType);
        
        List<PurchaseBill> bills = purchaseBillRepository.findByBillTypeOrderByBillDateDesc(billType);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getPendingApprovalBills(Long pumpId) {
        log.info("Getting pending approval bills for pump: {}", pumpId);
        
        List<PurchaseBill> bills = purchaseBillRepository.findPendingApprovalBills(pumpId);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getOverdueBills(Long pumpId) {
        log.info("Getting overdue bills for pump: {}", pumpId);
        
        List<PurchaseBill> bills = purchaseBillRepository.findOverdueBills(LocalDate.now(), pumpId);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillDTO> getBillsPendingReceipt(Long pumpId) {
        log.info("Getting bills pending receipt for pump: {}", pumpId);
        
        List<PurchaseBill> bills = purchaseBillRepository.findByStatusAndPumpIdOrderByBillDateDesc(PurchaseBill.Status.APPROVED, pumpId);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPurchaseBillDashboard(Long pumpId) {
        log.info("Getting purchase bill dashboard for pump: {}", pumpId);
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Count bills by status
        dashboard.put("totalBills", purchaseBillRepository.count());
        dashboard.put("draftBills", purchaseBillRepository.countByStatusAndPumpId(PurchaseBill.Status.DRAFT, pumpId));
        dashboard.put("submittedBills", purchaseBillRepository.countByStatusAndPumpId(PurchaseBill.Status.SUBMITTED, pumpId));
        dashboard.put("approvedBills", purchaseBillRepository.countByStatusAndPumpId(PurchaseBill.Status.APPROVED, pumpId));
        dashboard.put("receivedBills", purchaseBillRepository.countByStatusAndPumpId(PurchaseBill.Status.RECEIVED, pumpId));
        dashboard.put("invoicedBills", purchaseBillRepository.countByStatusAndPumpId(PurchaseBill.Status.INVOICED, pumpId));
        dashboard.put("paidBills", purchaseBillRepository.countByStatusAndPumpId(PurchaseBill.Status.PAID, pumpId));
        
        // Overdue bills
        List<PurchaseBillDTO> overdueBills = getOverdueBills(pumpId);
        dashboard.put("overdueBills", overdueBills.size());
        
        // Pending approval
        List<PurchaseBillDTO> pendingApproval = getPendingApprovalBills(pumpId);
        dashboard.put("pendingApproval", pendingApproval.size());
        
        return dashboard;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPurchaseBillSummaryByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting purchase bill summary by date range for pump: {}", pumpId);
        
        Map<String, Object> summary = new HashMap<>();
        
        List<PurchaseBillDTO> bills = getPurchaseBillsByDateRange(pumpId, startDate, endDate);
        
        BigDecimal totalAmount = bills.stream()
                .map(PurchaseBillDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        summary.put("totalBills", bills.size());
        summary.put("totalAmount", totalAmount);
        summary.put("bills", bills);
        
        return summary;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPurchaseBillSummaryBySupplier(Long pumpId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting purchase bill summary by supplier for pump: {}", pumpId);
        
        Map<String, Object> summary = new HashMap<>();
        
        // Group bills by supplier
        Map<String, List<PurchaseBillDTO>> billsBySupplier = getPurchaseBillsByDateRange(pumpId, startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(PurchaseBillDTO::getSupplierName));
        
        summary.put("supplierSummary", billsBySupplier);
        
        return summary;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPurchaseBillSummaryByFuelType(Long pumpId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting purchase bill summary by fuel type for pump: {}", pumpId);
        
        Map<String, Object> summary = new HashMap<>();
        
        // This would require joining with items table
        // For now, return basic summary
        List<PurchaseBillDTO> bills = getPurchaseBillsByDateRange(pumpId, startDate, endDate);
        summary.put("totalBills", bills.size());
        summary.put("bills", bills);
        
        return summary;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPurchaseBillSummaryByStatus(Long pumpId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting purchase bill summary by status for pump: {}", pumpId);
        
        Map<String, Object> summary = new HashMap<>();
        
        List<PurchaseBillDTO> bills = getPurchaseBillsByDateRange(pumpId, startDate, endDate);
        
        // Group by status
        Map<String, Long> statusCount = bills.stream()
                .collect(Collectors.groupingBy(
                    bill -> bill.getStatus().toString(),
                    Collectors.counting()
                ));
        
        summary.put("statusSummary", statusCount);
        summary.put("bills", bills);
        
        return summary;
    }
    
    @Override
    public String generateBillNumber(Long pumpId, PurchaseBill.BillType billType) {
        log.info("Generating bill number for pump: {} and type: {}", pumpId, billType);
        
        String prefix = "PB-" + billType.name().substring(0, 3) + "-";
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());
        
        // Find the last bill number for this month
        List<PurchaseBill> bills = purchaseBillRepository.findByPumpIdOrderByBillDateDesc(pumpId);
        int maxSequence = 0;
        
        for (PurchaseBill bill : bills) {
            String billNumber = bill.getBillNumber();
            if (billNumber != null && billNumber.startsWith(prefix + year + month)) {
                try {
                    int sequence = Integer.parseInt(billNumber.substring(billNumber.length() - 4));
                    maxSequence = Math.max(maxSequence, sequence);
                } catch (NumberFormatException e) {
                    // Ignore invalid bill numbers
                }
            }
        }
        
        return String.format("%s%s%s%04d", prefix, year, month, maxSequence + 1);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isBillNumberUnique(String billNumber, Long excludeId) {
        Optional<PurchaseBill> existingBill = purchaseBillRepository.findByBillNumber(billNumber);
        return existingBill.isEmpty() || existingBill.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canApproveBill(Long billId) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + billId));
        return PurchaseBill.Status.SUBMITTED.equals(bill.getStatus());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canReceiveBill(Long billId) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + billId));
        return PurchaseBill.Status.APPROVED.equals(bill.getStatus());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canPayBill(Long billId) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + billId));
        return PurchaseBill.Status.INVOICED.equals(bill.getStatus());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canCancelBill(Long billId) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase bill not found with id: " + billId));
        return !PurchaseBill.Status.PAID.equals(bill.getStatus()) && 
               !PurchaseBill.Status.CANCELLED.equals(bill.getStatus());
    }
    
    // Helper methods
    private void calculateBillTotals(PurchaseBill bill) {
        List<PurchaseBillItem> items = purchaseBillItemRepository.findByPurchaseBillIdOrderById(bill.getId());
        
        BigDecimal subTotal = items.stream()
                .map(PurchaseBillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal taxAmount = items.stream()
                .map(PurchaseBillItem::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal discountAmount = items.stream()
                .map(PurchaseBillItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        bill.setSubTotal(subTotal);
        bill.setTaxAmount(taxAmount);
        bill.setDiscountAmount(discountAmount);
        bill.calculateTotal();
    }
    
    private boolean canUpdateBill(PurchaseBill bill) {
        return PurchaseBill.Status.DRAFT.equals(bill.getStatus()) || 
               PurchaseBill.Status.SUBMITTED.equals(bill.getStatus());
    }
    
    private boolean canDeleteBill(PurchaseBill bill) {
        return PurchaseBill.Status.DRAFT.equals(bill.getStatus());
    }
    
    private PurchaseBill convertToEntity(PurchaseBillDTO dto) {
        return PurchaseBill.builder()
                .id(dto.getId())
                .billNumber(dto.getBillNumber())
                .billDate(dto.getBillDate())
                .pumpId(dto.getPumpId())
                .billType(dto.getBillType())
                .status(dto.getStatus() != null ? dto.getStatus() : PurchaseBill.Status.DRAFT)
                .subTotal(dto.getSubTotal())
                .taxAmount(dto.getTaxAmount())
                .discountAmount(dto.getDiscountAmount())
                .totalAmount(dto.getTotalAmount())
                .paymentTerms(dto.getPaymentTerms())
                .dueDate(dto.getDueDate())
                .paymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : PurchaseBill.PaymentStatus.PENDING)
                .notes(dto.getNotes())
                .referenceNumber(dto.getReferenceNumber())
                .vehicleNumber(dto.getVehicleNumber())
                .driverName(dto.getDriverName())
                .approvedBy(dto.getApprovedBy())
                .approvedAt(dto.getApprovedAt())
                .build();
    }
    
    private PurchaseBillItem convertToItemEntity(PurchaseBillItemDTO dto) {
        return PurchaseBillItem.builder()
                .id(dto.getId())
                .itemDescription(dto.getItemDescription())
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .totalPrice(dto.getTotalPrice())
                .taxRate(dto.getTaxRate())
                .taxAmount(dto.getTaxAmount())
                .discountRate(dto.getDiscountRate())
                .discountAmount(dto.getDiscountAmount())
                .netAmount(dto.getNetAmount())
                .receivedQuantity(dto.getReceivedQuantity())
                .remainingQuantity(dto.getRemainingQuantity())
                .notes(dto.getNotes())
                .build();
    }
    
    private PurchaseBillDTO convertToDTO(PurchaseBill entity) {
        PurchaseBillDTO dto = PurchaseBillDTO.builder()
                .id(entity.getId())
                .billNumber(entity.getBillNumber())
                .billDate(entity.getBillDate())
                .supplierId(entity.getSupplier() != null ? entity.getSupplier().getId() : null)
                .supplierName(entity.getSupplier() != null ? entity.getSupplier().getSupplierName() : null)
                .supplierCode(entity.getSupplier() != null ? entity.getSupplier().getSupplierCode() : null)
                .pumpId(entity.getPumpId())
                .billType(entity.getBillType())
                .status(entity.getStatus())
                .subTotal(entity.getSubTotal())
                .taxAmount(entity.getTaxAmount())
                .discountAmount(entity.getDiscountAmount())
                .totalAmount(entity.getTotalAmount())
                .paymentTerms(entity.getPaymentTerms())
                .dueDate(entity.getDueDate())
                .paymentStatus(entity.getPaymentStatus())
                .notes(entity.getNotes())
                .referenceNumber(entity.getReferenceNumber())
                .vehicleNumber(entity.getVehicleNumber())
                .driverName(entity.getDriverName())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        
        // Set display fields
        dto.setStatusDisplay(entity.getStatus().toString());
        dto.setPaymentStatusDisplay(entity.getPaymentStatus().toString());
        dto.setBillTypeDisplay(entity.getBillType().toString());
        dto.setSupplierDisplay(entity.getSupplier() != null ? entity.getSupplier().getSupplierName() : null);
        
        // Load items
        List<PurchaseBillItemDTO> items = purchaseBillItemRepository.findByPurchaseBillIdOrderById(entity.getId())
                .stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
        dto.setItems(items);
        
        return dto;
    }
    
    private PurchaseBillItemDTO convertToItemDTO(PurchaseBillItem entity) {
        PurchaseBillItemDTO dto = PurchaseBillItemDTO.builder()
                .id(entity.getId())
                .fuelTypeId(entity.getFuelType() != null ? entity.getFuelType().getId() : null)
                .fuelTypeName(entity.getFuelType() != null ? entity.getFuelType().getName() : null)
                .itemDescription(entity.getItemDescription())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .taxRate(entity.getTaxRate())
                .taxAmount(entity.getTaxAmount())
                .discountRate(entity.getDiscountRate())
                .discountAmount(entity.getDiscountAmount())
                .netAmount(entity.getNetAmount())
                .receivedQuantity(entity.getReceivedQuantity())
                .remainingQuantity(entity.getRemainingQuantity())
                .notes(entity.getNotes())
                .build();
        
        // Set status fields
        dto.setFullyReceived(entity.isFullyReceived());
        dto.setPartiallyReceived(entity.isPartiallyReceived());
        
        return dto;
    }
    
    private void updateBillFields(PurchaseBill existing, PurchaseBillDTO dto) {
        existing.setBillNumber(dto.getBillNumber());
        existing.setBillDate(dto.getBillDate());
        existing.setPumpId(dto.getPumpId());
        existing.setBillType(dto.getBillType());
        existing.setPaymentTerms(dto.getPaymentTerms());
        existing.setDueDate(dto.getDueDate());
        existing.setNotes(dto.getNotes());
        existing.setReferenceNumber(dto.getReferenceNumber());
        existing.setVehicleNumber(dto.getVehicleNumber());
        existing.setDriverName(dto.getDriverName());
    }
}
