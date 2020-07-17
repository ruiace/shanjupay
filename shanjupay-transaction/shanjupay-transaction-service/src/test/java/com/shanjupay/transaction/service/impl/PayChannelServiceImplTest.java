package com.shanjupay.transaction.service.impl;

import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PayChannelServiceImplTest {

    @Autowired
    private PayChannelService payChannelService;

    @Test
    public void testQueryPayChannelByPlatformChannel(){
        List<PayChannelDTO> list = payChannelService.queryPayChannelByPlatformChannel("shanju_b2c");
        for (PayChannelDTO payChannelDTO : list) {
            System.out.println(payChannelDTO);
        }

    }
}