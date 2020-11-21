package com.nowcoder.work.community.controller;

import com.nowcoder.work.community.service.DataService;
import com.nowcoder.work.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDatePage(){
        return "/site/admin/data";
    }

    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model){
        long uv = dataService.calculateUV(start, end);
        Map<String, Object> map = new HashMap<>();
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        return "forward:/data";
    }

    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                         Model model){
        long dau = dataService.calculateDAU(start, end);
        Map<String, Object> map = new HashMap<>();
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "forward:/data";
    }

}
