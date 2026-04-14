package edu.kust.facedeepseek.util;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class TimeRangeUtil {
    /**
     * 根据时间范围类型，获取开始时间和结束时间
     * @param timeRange 范围类型：today/week/month/year
     * @return 长度为2的数组：[startTime, endTime]
     */
    public static LocalDateTime[] getTimeRange(String timeRange) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = null;

        switch (timeRange.toLowerCase()) {
            case "today":
                // 今日：从00:00:00到当前时间
                startTime = endTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "week":
                // 本周：从周一00:00到当前时间（若需周日开始可调整）
                startTime = endTime.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "month":
                // 本月：从1号00:00到当前时间
                startTime = endTime.with(TemporalAdjusters.firstDayOfMonth())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "year":
                // 全年：从1月1号00:00到当前时间
                startTime = endTime.with(TemporalAdjusters.firstDayOfYear())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            default:
                // 默认返回今日
                startTime = endTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        return new LocalDateTime[]{startTime, endTime};
    }

    /**
     * 获取上一个周期的时间范围（用于计算环比）
     * @param timeRange 当前周期类型
     * @return 上一周期的[startTime, endTime]
     */
    public static LocalDateTime[] getLastPeriodRange(String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastEnd = null;
        LocalDateTime lastStart = null;

        switch (timeRange.toLowerCase()) {
            case "today":
                // 上一日：昨天00:00到23:59:59
                lastEnd = now.minusDays(1).withHour(23).withMinute(59).withSecond(59);
                lastStart = now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
                break;
            case "week":
                // 上一周：前周一到上周日
                lastEnd = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                        .minusDays(1).withHour(23).withMinute(59).withSecond(59);
                lastStart = lastEnd.minusDays(6).withHour(0).withMinute(0).withSecond(0);
                break;
            case "month":
                // 上一月：上月1号到上月最后一天
                lastEnd = now.with(TemporalAdjusters.firstDayOfMonth())
                        .minusDays(1).withHour(23).withMinute(59).withSecond(59);
                lastStart = lastEnd.with(TemporalAdjusters.firstDayOfMonth())
                        .withHour(0).withMinute(0).withSecond(0);
                break;
            case "year":
                // 上一年：上年1月1号到12月31号
                lastEnd = now.with(TemporalAdjusters.firstDayOfYear())
                        .minusDays(1).withHour(23).withMinute(59).withSecond(59);
                lastStart = lastEnd.with(TemporalAdjusters.firstDayOfYear())
                        .withHour(0).withMinute(0).withSecond(0);
                break;
        }
        return new LocalDateTime[]{lastStart, lastEnd};
    }
}