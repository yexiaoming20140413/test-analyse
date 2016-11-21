package com.chouti.analyse.action;

import com.chouti.analyse.segment.ChoutiSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-09.
 *******************************************************************************/
@Controller
@RequestMapping("/api")
public class TestAction {
    private Logger log = LoggerFactory.getLogger(TestAction.class);

    @RequestMapping("/test")
    @ResponseBody
    Object index() throws IOException {
        ChoutiSegment choutiSegment = new ChoutiSegment();
        String text = "程序员(英文Programmer)是从事程序开发、维护的专业人员。一般将程序员分为程序设计人员和程序编码人员，但两者的界限并不非常清楚，特别是在中国。软件从业人员分为初级程序员、高级程序员、系统分析员和项目经理四大类";
        return choutiSegment.segment(text);
    }

}
