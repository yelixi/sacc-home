package org.sacc.SaccHome.mbg.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sacc.SaccHome.mbg.model.File;
import org.sacc.SaccHome.mbg.model.FileTask;
import org.sacc.SaccHome.mbg.model.FileTaskExample;
import org.springframework.stereotype.Component;

@Mapper
public interface FileTaskMapper {
    long countByExample(FileTaskExample example);

    int deleteByExample(FileTaskExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(FileTask record);

    int insertSelective(FileTask record);

    List<FileTask> selectByExample(FileTaskExample example);

    FileTask selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") FileTask record, @Param("example") FileTaskExample example);

    int updateByExample(@Param("record") FileTask record, @Param("example") FileTaskExample example);

    int updateByPrimaryKeySelective(FileTask record);

    int updateByPrimaryKey(FileTask record);

    List<File> getFilesByFileTaskId(Integer id);
}