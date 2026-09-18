package com.hzsun.aidevops.common.infrastructure.id;

import com.hzsun.aidevops.common.infrastructure.mapper.WorkerIdAllocatorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于数据库单行计数器的 workerId 分配器。
 *
 * <p>实例启动时在事务内完成“自增 + 读取”，依靠行锁保证并发启动互斥。
 * 当前策略不维护实例注册表、不做心跳续租，接受滚动发布或并发扩缩容导致 workerId 冲突的风险。</p>
 */
@Component
@RequiredArgsConstructor
public class DatabaseWorkerIdAllocator {

    private final WorkerIdAllocatorMapper workerIdAllocatorMapper;

    /**
     * 分配当前实例的 workerId。
     *
     * @return workerId，取值 0~31
     */
    @Transactional(rollbackFor = Exception.class)
    public int allocate() {
        workerIdAllocatorMapper.increaseWorkerId();
        return workerIdAllocatorMapper.currentWorkerId();
    }
}
