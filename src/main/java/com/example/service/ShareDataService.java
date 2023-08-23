package com.example.service;

import com.example.entity.ShareData;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ShareDataMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ShareDataService extends ServiceImpl<ShareDataMapper, ShareData> {

    @Resource
    private ShareDataMapper shareDataMapper;

}
