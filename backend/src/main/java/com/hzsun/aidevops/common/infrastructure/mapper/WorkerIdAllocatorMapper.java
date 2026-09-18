package com.hzsun.aidevops.common.infrastructure.mapper;

/**
 * 雪花算法 workerId 分配计数器 Mapper。
 *
 * <p>仅维护单行计数器；分配逻辑必须在事务内完成，以确保并发启动时单行更新互斥。</p>
 */
public interface WorkerIdAllocatorMapper {

    /**
     * 将当前 workerId 自增 1，超过 31 时重置为 0。
     *
     * @return 受影响行数
     */
    int increaseWorkerId();

    /**
     * 读取当前 workerId。
     *
     * @return 当前 workerId
     */
    int currentWorkerId();
}
