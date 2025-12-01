package com.campus.service;

import com.campus.entity.Trade;
import com.campus.entity.TradeCreateDTO;
import com.campus.entity.TradePageQueryDTO;
import com.campus.utils.PageResult;

public interface TradeService {
    //List<Trade> getTrades(String status);

    Trade createTrade(TradeCreateDTO tradeCreateDTO);

    PageResult pageQuery(TradePageQueryDTO tradePageQueryDTO, Long currentUserId);

    Trade getTradeById(Long id);
    Trade updateTradeStatus(Long id, String status);
}
