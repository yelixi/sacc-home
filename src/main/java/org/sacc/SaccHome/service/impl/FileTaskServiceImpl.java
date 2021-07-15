package org.sacc.SaccHome.service.impl;

import com.github.pagehelper.PageHelper;
import com.sun.jdi.InternalException;
import io.swagger.annotations.Api;
import org.sacc.SaccHome.mbg.mapper.FileTaskMapper;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.mbg.model.FileTaskExample;
import org.sacc.SaccHome.service.FileTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileTaskServiceImpl implements FileTaskService {
    @Autowired
    private FileTaskMapper fileTaskMapper;

    @Override
    public int createFileTask(FileTask fileTask){
        fileTask.setCreatedAt(LocalDateTime.now());
        fileTask.setUpdatedAt(LocalDateTime.now());
        int i = fileTaskMapper.insertSelective(fileTask);
        return i;
    }

    @Override
    public int updateFileTask(Integer id, FileTask fileTask) {
        fileTask.setUpdatedAt(LocalDateTime.now());
        fileTask.setId(id);
        return fileTaskMapper.updateByPrimaryKeySelective(fileTask);
    }

    @Override
    public int deleteFileTask(Integer id) {
        return fileTaskMapper.deleteByPrimaryKey(id);
    }

    @Override
    public FileTask getFileTask(Integer id) {
        return fileTaskMapper.selectByPrimaryKey(id);
    }



}
