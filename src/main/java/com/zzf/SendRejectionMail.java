package com.zzf;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * 实现 flowable触发器
 */
public class SendRejectionMail implements JavaDelegate {

    /***
     * flowable触发器 触发方法
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {

        System.out.println("不好意思，" + delegateExecution.getParent().getVariable("employeeName") + "。你的请假流程被拒绝了。");
    }
}
