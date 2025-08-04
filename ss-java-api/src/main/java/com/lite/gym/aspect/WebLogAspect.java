package com.lite.gym.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class WebLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(public * com.lite.gym.controller..*.*(..))")
    public void webLog() {}

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取 request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        // 打印请求信息
        if (request != null) {
            System.out.println("请求URL: " + request.getRequestURL());
            System.out.println("请求方式: " + request.getMethod());
            System.out.println("请求IP: " + request.getRemoteAddr());
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        System.out.println("请求类方法: " + signature.getDeclaringTypeName() + "." + signature.getName());
        // 打印请求参数，跳过MultipartFile、ServletRequest等不可序列化类型
        Object[] args = joinPoint.getArgs();
        Object[] filteredArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;
                filteredArgs[i] = "MultipartFile[name=" + file.getOriginalFilename() + ", size=" + file.getSize() + "]";
            } else if (arg instanceof MultipartFile[]) {
                MultipartFile[] files = (MultipartFile[]) arg;
                StringBuilder sb = new StringBuilder();
                sb.append("MultipartFile[][");
                for (MultipartFile file : files) {
                    sb.append("{name=").append(file.getOriginalFilename())
                      .append(", size=").append(file.getSize()).append("}, ");
                }
                sb.append("]");
                filteredArgs[i] = sb.toString();
            } else if (arg instanceof javax.servlet.ServletRequest) {
                filteredArgs[i] = "ServletRequest[" + arg.getClass().getSimpleName() + "]";
            } else {
                filteredArgs[i] = arg;
            }
        }
        System.out.println("请求参数: " + objectMapper.writeValueAsString(filteredArgs));

        // 执行方法
        Object result = joinPoint.proceed();

        // 打印响应信息
        System.out.println("返回结果: " + objectMapper.writeValueAsString(result));
        System.out.println("耗时: " + (System.currentTimeMillis() - startTime) + " ms");
        return result;
    }
} 