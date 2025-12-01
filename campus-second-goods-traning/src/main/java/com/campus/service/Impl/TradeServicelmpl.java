package com.campus.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.Trade;
import com.campus.entity.TradeCreateDTO;
import com.campus.entity.TradePageQueryDTO;
import com.campus.mapper.TradeMapper;
import com.campus.utils.CurrentHolder;
import com.campus.utils.PageResult;
import com.campus.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TradeServicelmpl  implements TradeService {
    @Autowired
    private TradeMapper tradeMapper;

    /*
    @Override
    public List<Trade> getTrades(String status) {

        return tradeMapper.selectTrades(status);
    }*/

    @Override
    public Trade createTrade(TradeCreateDTO tradeCreateDTO) {
        System.out.println("创建交易");
        Trade trade = new Trade();

        trade.setProductId(tradeCreateDTO.getId());

        Long currentUserId = CurrentHolder.getCurrentId();
        // 添加空值检查
        if (currentUserId == null) {
            throw new IllegalStateException("无法获取当前用户ID");
        }

        trade.setBuyerId(currentUserId);
        trade.setSellerId(tradeCreateDTO.getSeller_id());
        trade.setProductImage(tradeCreateDTO.getImage_url());
        trade.setProductPrice(tradeCreateDTO.getPrice());
        trade.setProductTitle(tradeCreateDTO.getName());
        trade.setShippingAddress(tradeCreateDTO.getTrade_localtion());

        trade.setStatus("PENDING");
        trade.setQuantity(1);
        trade.setTotalAmount(trade.getProductPrice()*trade.getQuantity());
        trade.setCreatedAt(LocalDateTime.now());
        trade.setUpdatedAt(LocalDateTime.now());
        tradeMapper.insertOneTrade(trade);

        return trade;
    }

    @Override
    public PageResult pageQuery(TradePageQueryDTO tradePageQueryDTO,Long currentUserId) {
        Page<Trade> page = new Page<>(tradePageQueryDTO.getPage(), tradePageQueryDTO.getPageSize());

        System.out.println("执行分页查询，参数: " + tradePageQueryDTO);

        IPage<Trade> tradePage = tradeMapper.pageQuery(page, tradePageQueryDTO,currentUserId);

        System.out.println("查询结果总数: " + tradePage.getTotal());
        System.out.println("查询结果记录数: " + tradePage.getRecords().size());

        PageResult pageResult = new PageResult();
        pageResult.setTotal(tradePage.getTotal());
        pageResult.setRecords(tradePage.getRecords());
        return pageResult;
    }

    @Override
    public Trade getTradeById(Long id) {
        return tradeMapper.selectTradeById(id);
    }

    @Override
    public Trade updateTradeStatus(Long id, String status) {
        tradeMapper.updateTradeStatus(id, status);
        return tradeMapper.selectTradeById(id);
    }
}
