package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.ShareData;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.ShareDataService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/api/shareData")
public class ShareDataController {
    @Resource
    private ShareDataService shareDataService;
    @Resource
    private HttpServletRequest request;

    public User getUser() {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomException("-1", "请登录");
        }
        return user;
    }

    @PostMapping
    public Result<?> save(@RequestBody ShareData shareData) {
        return Result.success(shareDataService.save(shareData));
    }

    @PutMapping
    public Result<?> update(@RequestBody ShareData shareData) {
        return Result.success(shareDataService.updateById(shareData));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        shareDataService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(shareDataService.getById(id));
    }

    @GetMapping
    public Result<?> findAll() {
        return Result.success(shareDataService.list());
    }

    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                              @RequestParam(required = false, defaultValue = "") String date) {
        LambdaQueryWrapper<ShareData> query = Wrappers.<ShareData>lambdaQuery().orderByDesc(ShareData::getId);
        if (StrUtil.isNotBlank(name)) {
            query.and(wrapper -> wrapper.like(ShareData::getRequireDepart, name)
                    .or().like(ShareData::getRequireManager, name)
                    .or().like(ShareData::getSourceDepart, name)
                    .or().like(ShareData::getSourceManager, name));
        }
        if (StrUtil.isNotBlank(date)) {
            query.eq(ShareData::getTime, date);
        }
        return Result.success(shareDataService.page(new Page<>(pageNum, pageSize), query));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<ShareData> all = shareDataService.list();
        for (ShareData obj : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("", obj.getId());
            row.put("需求部门", obj.getRequireDepart());
            row.put("需求部门数据资产管理员", obj.getRequireManager());
            row.put("需求指标", obj.getRequireTarget());
            row.put("需求信息", obj.getRequireMessage());
            row.put("数据", obj.getMonthData());
            row.put("单位", obj.getUnit());
            row.put("数据来源部门", obj.getSourceDepart());
            row.put("来源部门数据资产管理员", obj.getSourceManager());
            row.put("频次", obj.getFrequency());
            row.put("用途", obj.getFun());

            list.add(row);
        }

        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String month = DateUtil.format(new Date(), DatePattern.SIMPLE_MONTH_PATTERN);
        String fileName = URLEncoder.encode(month + "需求统计信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }

    @GetMapping("/upload/{fileId}")
    public Result<?> upload(@PathVariable String fileId) {
        String basePath = System.getProperty("user.dir") + "/src/main/resources/static/file/";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String file = fileNames.stream().filter(name -> name.contains(fileId)).findAny().orElse("");
        List<List<Object>> lists = ExcelUtil.getReader(basePath + file).read(1);
        List<ShareData> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            ShareData obj = new ShareData();
            obj.setRequireDepart((String) row.get(1));
            obj.setRequireManager((String) row.get(2));
            obj.setRequireTarget((String) row.get(3));
            obj.setRequireMessage((String) row.get(4));
            obj.setMonthData((String) row.get(5));
            obj.setUnit((String) row.get(6));
            obj.setSourceDepart((String) row.get(7));
            obj.setSourceManager((String) row.get(8));
            obj.setFrequency((String) row.get(9));
            obj.setFun((String) row.get(10));

            saveList.add(obj);
        }
        shareDataService.saveBatch(saveList);
        return Result.success();
    }

}
