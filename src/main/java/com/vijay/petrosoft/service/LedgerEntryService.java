package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.LedgerEntry;
import com.vijay.petrosoft.dto.LedgerEntryDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LedgerEntryService {
    LedgerEntryDTO createLedgerEntry(LedgerEntryDTO ledgerEntryDTO);
    LedgerEntryDTO updateLedgerEntry(Long id, LedgerEntryDTO ledgerEntryDTO);
    Optional<LedgerEntryDTO> getLedgerEntryById(Long id);
    List<LedgerEntryDTO> getAllLedgerEntries();
    void deleteLedgerEntry(Long id);
    List<LedgerEntryDTO> getLedgerEntriesByPumpId(Long pumpId);
    List<LedgerEntryDTO> getLedgerEntriesByAccountId(Long accountId);
    List<LedgerEntryDTO> getLedgerEntriesByDateRange(LocalDate startDate, LocalDate endDate);
    BigDecimal getAccountBalance(Long accountId, LocalDate asOfDate);
    List<LedgerEntryDTO> getAccountStatement(Long accountId, LocalDate startDate, LocalDate endDate);
}
