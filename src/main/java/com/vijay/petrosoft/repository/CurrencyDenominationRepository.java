package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.CurrencyDenomination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyDenominationRepository extends JpaRepository<CurrencyDenomination, Long> {

    List<CurrencyDenomination> findByPumpIdOrderByDenominationValueDesc(Long pumpId);

    List<CurrencyDenomination> findByShiftIdOrderByDenominationValueDesc(Long shiftId);

    @Query("SELECT cd FROM CurrencyDenomination cd WHERE cd.pumpId = :pumpId AND cd.shiftId = :shiftId ORDER BY cd.denominationValue DESC")
    List<CurrencyDenomination> findByPumpIdAndShiftId(@Param("pumpId") Long pumpId, @Param("shiftId") Long shiftId);

    @Query("SELECT cd FROM CurrencyDenomination cd WHERE cd.currencyType = :currencyType ORDER BY cd.createdAt DESC")
    List<CurrencyDenomination> findByCurrencyType(@Param("currencyType") CurrencyDenomination.CurrencyType currencyType);

    @Query("SELECT cd FROM CurrencyDenomination cd WHERE cd.pumpId = :pumpId AND cd.currencyType = :currencyType ORDER BY cd.createdAt DESC")
    List<CurrencyDenomination> findByPumpIdAndCurrencyType(@Param("pumpId") Long pumpId, 
                                                           @Param("currencyType") CurrencyDenomination.CurrencyType currencyType);

    @Query("SELECT SUM(cd.totalAmount) FROM CurrencyDenomination cd WHERE cd.shiftId = :shiftId")
    Double sumTotalAmountByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(cd.totalAmount) FROM CurrencyDenomination cd WHERE cd.shiftId = :shiftId AND cd.currencyType IN ('NOTE_2000', 'NOTE_500', 'NOTE_200', 'NOTE_100', 'NOTE_50', 'NOTE_20', 'NOTE_10')")
    Double sumNotesTotalByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(cd.totalAmount) FROM CurrencyDenomination cd WHERE cd.shiftId = :shiftId AND cd.currencyType IN ('COIN_10', 'COIN_5', 'COIN_2', 'COIN_1', 'OTHER_COINS')")
    Double sumCoinsTotalByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT cd FROM CurrencyDenomination cd WHERE cd.pumpId = :pumpId AND cd.shiftId = :shiftId AND cd.currencyType = :currencyType")
    Optional<CurrencyDenomination> findByPumpShiftAndCurrencyType(@Param("pumpId") Long pumpId, 
                                                                  @Param("shiftId") Long shiftId, 
                                                                  @Param("currencyType") CurrencyDenomination.CurrencyType currencyType);

    @Query("SELECT cd FROM CurrencyDenomination cd WHERE cd.countedBy = :userId ORDER BY cd.createdAt DESC")
    List<CurrencyDenomination> findByCountedBy(@Param("userId") Long userId);

    @Query("SELECT cd FROM CurrencyDenomination cd WHERE cd.verifiedBy = :userId ORDER BY cd.createdAt DESC")
    List<CurrencyDenomination> findByVerifiedBy(@Param("userId") Long userId);
}
