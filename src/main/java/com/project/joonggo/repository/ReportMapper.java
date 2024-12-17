package com.project.joonggo.repository;

import com.project.joonggo.domain.PagingVO;
import com.project.joonggo.domain.ReasonVO;
import com.project.joonggo.domain.ReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper {
    List<ReasonVO> getReasonList();

    void insertReport(ReportVO reportVO);

    int getTotalCount(PagingVO pgvo);

    List<ReportVO> getList(PagingVO pgvo);

    void updateStatus(@Param("reportId") Long reportId, @Param("status") String status);

    String getStatusByReportId(Long reportId);

    Long getUserNumByReportId(Long reportId);

    String getCompContentByCompId(long reportCompId);
}
