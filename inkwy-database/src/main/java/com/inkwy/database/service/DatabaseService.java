package com.inkwy.database.service;

import com.inkwy.database.dto.CompareAllResultDto;
import com.inkwy.database.dto.CompareResultDto;
import com.inkwy.database.param.DatabaseParam;

import java.util.List;

/**
 * 源数据库service
 */
public interface DatabaseService {
    /**
     * 已知schema比较两个库表差异
     * @param databaseParam
     * @return
     */
    List<CompareAllResultDto> compareBySchema(DatabaseParam databaseParam);

    /**
     * 未知schema直接比较两个库表差异
     * @param databaseParam
     * @return
     */
    List<CompareAllResultDto> compare(DatabaseParam databaseParam);
}
