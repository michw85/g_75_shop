package de.ait.g_75_shop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Aspect for logging service layer operations
 * Provides detailed logging for method entry, exit, exceptions, and execution time
 * <p>
 * Аспект для логирования операций сервисного слоя
 * Предоставляет детальное логирование входа/выхода из методов, исключений и времени выполнения
 */
@Aspect
@Component
public class AspectLogging {

    private final Logger logger = LoggerFactory.getLogger(AspectLogging.class);

    /**
     * Pointcut for all methods in ProductServiceImpl
     * Pointcut для всех методов в ProductServiceImpl
     */
    @Pointcut("execution(* de.ait.g_75_shop.service.ProductServiceImpl.*(..))")
    public void anyMethodInProductService() {
    }

    /**
     * Logs before any method execution in ProductServiceImpl
     * Логирует перед выполнением любого метода в ProductServiceImpl
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     */
    @Before("anyMethodInProductService()")
    public void beforeAnyMethodInProductService(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.debug("Method {} of the class ProductServiceImpl called with arguments: {}", methodName, Arrays.toString(args));
    }

    /**
     * Logs after any method execution in ProductServiceImpl
     * Логирует после выполнения любого метода в ProductServiceImpl
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     */
    @After("anyMethodInProductService()")
    public void afterAnyMethodInProductService(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class ProductServiceImpl finished its work", methodName);
    }

    /**
     * Logs after successful method execution in ProductServiceImpl
     * Логирует после успешного выполнения метода в ProductServiceImpl
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     * @param result    the result returned by the method / результат, возвращенный методом
     */
    @AfterReturning(pointcut = "anyMethodInProductService()", returning = "result")
    public void afterReturningAnyMethodInProductService(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class ProductServiceImpl returned result: {}", methodName, result);
    }

    /**
     * Logs when method throws an exception in ProductServiceImpl
     * Логирует, когда метод выбрасывает исключение в ProductServiceImpl
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     * @param e         the exception thrown / выброшенное исключение
     */
    @AfterThrowing(pointcut = "anyMethodInProductService()", throwing = "e")
    public void afterThrowingAnyMethodInProductService(JoinPoint joinPoint, Exception e) {
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class ProductServiceImpl threw new exception", methodName, e);
    }

    /**
     * Pointcut for all methods in all service classes
     * Catches all public methods in classes implementing Service interfaces
     * <p>
     * Pointcut для всех методов во всех сервисах пакета service
     * Перехватывает все public методы в классах, реализующих интерфейсы Service
     */
    @Pointcut("execution(* de.ait.g_75_shop.service.*.*(..))")
    public void anyServiceMethod() {
    }

    /**
     * Pointcut for data modification methods (save, update, delete, add, remove, clear)
     * Pointcut для методов, изменяющих данные (save, update, delete, add, remove, clear)
     */
    @Pointcut("execution(* de.ait.g_75_shop.service.*.save*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.update*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.delete*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.add*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.remove*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.clear*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.restore*(..))")
    public void dataModificationMethods() {
    }

    /**
     * Pointcut for data read methods (get, getAll, find, count)
     * Pointcut для методов чтения данных (get, getAll, find, count)
     */
    @Pointcut("execution(* de.ait.g_75_shop.service.*.get*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.find*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.count*(..)) || " +
            "execution(* de.ait.g_75_shop.service.*.is*(..))")
    public void dataReadMethods() {
    }

    /**
     * Logs BEFORE execution of any service method (DEBUG level)
     * Логирование ДО выполнения любого метода в сервисе (DEBUG уровень)
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     */
    @Before("anyServiceMethod()")
    public void logBeforeAnyServiceMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Determine operation type / Определяем тип операции
        String operationType = determineOperationType(methodName);

        if (args.length > 0) {
            logger.debug("🔵 [{}.{}] Вход в метод. Операция: {}. Параметры: {}",
                    className, methodName, operationType, maskSensitiveData(args));
        } else {
            logger.debug("🔵 [{}.{}] Вход в метод. Операция: {}. Без параметров",
                    className, methodName, operationType);
        }
    }

    /**
     * Logs AFTER successful method execution (DEBUG level)
     * Логирование ПОСЛЕ успешного выполнения метода (DEBUG уровень)
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     * @param result    the result returned by the method / результат, возвращенный методом
     */
    @AfterReturning(pointcut = "anyServiceMethod()", returning = "result")
    public void logAfterSuccessfulMethod(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Логируем результат в зависимости от его типа
        String resultLog = formatResultForLogging(result);

        logger.debug("🟢 [{}.{}] Метод успешно выполнен. Результат: {}",
                className, methodName, resultLog);
    }

    /**
     * Logs when exception occurs (ERROR level)
     * Логирование при возникновении исключения (ERROR уровень)
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     * @param exception the exception thrown / выброшенное исключение
     */
    @AfterThrowing(pointcut = "anyServiceMethod()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        logger.error("🔴 [{}.{}] Ошибка при выполнении: {}",
                className, methodName, exception.getMessage(), exception);
    }

    /**
     * Logs for data modification methods (INFO level - business events)
     * Логирование для методов, изменяющих данные (INFO уровень - бизнес-события)
     *
     * @param joinPoint join point providing method information / точка соединения с информацией о методе
     * @param result    the result returned by the method / результат, возвращенный методом
     */
    @AfterReturning(pointcut = "dataModificationMethods()", returning = "result")
    public void logDataModification(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Формируем сообщение о бизнес-событии
        String businessEvent = createBusinessEventMessage(className, methodName, args, result);

        // Используем INFO уровень для бизнес-событий
        logger.info("📊 БИЗНЕС-СОБЫТИЕ: {}", businessEvent);
    }

    /**
     * Logs execution time for all methods
     * Логирование времени выполнения для всех методов
     *
     * @param joinPoint proceeding join point / продолжающаяся точка соединения
     * @return method execution result / результат выполнения метода
     * @throws Throwable if method throws exception / если метод выбрасывает исключение
     */
    @Around("anyServiceMethod()")
    public Object logExecutionTime(org.aspectj.lang.ProceedingJoinPoint joinPoint)
            throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            if (executionTime > 1000) {
                logger.warn("⚠ [{}.{}] Медленный метод! Время выполнения: {} мс",
                        className, methodName, executionTime);
            } else {
                logger.debug("⏱ [{}.{}] Время выполнения: {} мс",
                        className, methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            logger.error("❌ [{}.{}] Ошибка во время выполнения ({} мс)",
                    className, methodName, System.currentTimeMillis() - startTime, e);
            throw e;
        }
    }

// =============== СПЕЦИАЛИЗИРОВАННЫЕ МЕТОДЫ ДЛЯ РАЗНЫХ СЕРВИСОВ ===============
    // =============== SPECIALIZED METHODS FOR DIFFERENT SERVICES ===============

    /**
     * Specialized logging for ProductService save operations
     * Специализированное логирование для операций сохранения в ProductService
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.interfaces.ProductService.save(..))",
            returning = "result")
    public void logProductSaved(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        logger.info("📦 НОВЫЙ ТОВАР: Сохранен товар: {}", result);
    }

    /**
     * Specialized logging for ProductService update operations
     * Специализированное логирование для операций обновления в ProductService
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.ProductService.update(..))")
    public void logProductUpdated(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 2) {
            logger.info("📦 ОБНОВЛЕНИЕ ТОВАРА: ID={}, новые параметры: {}",
                    args[0], args[1]);
        }
    }

    /**
     * Specialized logging for ProductService delete operations
     * Специализированное логирование для операций удаления в ProductService
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.ProductService.deleteById(..))")
    public void logProductDeleted(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("📦 УДАЛЕНИЕ ТОВАРА (soft delete): ID={}", args[0]);
        }
    }

    /**
     * Specialized logging for ProductService restore operations
     * Специализированное логирование для операций восстановления в ProductService
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.ProductService.restoreById(..))")
    public void logProductRestored(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("📦 ВОССТАНОВЛЕНИЕ ТОВАРА: ID={}", args[0]);
        }
    }

    /**
     * Specialized logging for CustomerService save operations
     * Специализированное логирование для операций сохранения в CustomerService
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.interfaces.CustomerService.save(..))",
            returning = "result")
    public void logCustomerSaved(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        logger.info("👤 НОВЫЙ ПОКУПАТЕЛЬ: Сохранен покупатель: {}", result);
    }

    /**
     * Specialized logging for CustomerService update operations
     * Специализированное логирование для операций обновления в CustomerService
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.CustomerService.update(..))")
    public void logCustomerUpdated(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 2) {
            logger.info("👤 ОБНОВЛЕНИЕ ПОКУПАТЕЛЯ: ID={}, новые параметры: {}",
                    args[0], args[1]);
        }
    }

    /**
     * Specialized logging for CustomerService delete operations
     * Специализированное логирование для операций удаления в CustomerService
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.CustomerService.deleteById(..))")
    public void logCustomerDeleted(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("👤 УДАЛЕНИЕ ПОКУПАТЕЛЯ (soft delete): ID={}", args[0]);
        }
    }

    /**
     * Specialized logging for CustomerService restore operations
     * Специализированное логирование для операций восстановления в CustomerService
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.CustomerService.restoreById(..))")
    public void logCustomerRestored(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("👤 ВОССТАНОВЛЕНИЕ ПОКУПАТЕЛЯ: ID={}", args[0]);
        }
    }

    /**
     * Logging for cart operations (add to cart)
     * Логирование операций с корзиной (добавление в корзину)
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.CustomerService.addProductToCart(..))")
    public void logProductAddedToCart(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 3) {
            logger.info("🛒 ДОБАВЛЕНИЕ В КОРЗИНУ: Покупатель ID={}, Товар ID={}, Количество={}",
                    args[0], args[1], args[2]);
        }
    }

    /**
     * Logging for cart operations (remove from cart)
     * Логирование операций с корзиной (удаление из корзины)
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.CustomerService.removeProductFromCart(..))")
    public void logProductRemovedFromCart(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 2) {
            logger.info("🛒 УДАЛЕНИЕ ИЗ КОРЗИНЫ: Покупатель ID={}, Товар ID={}",
                    args[0], args[1]);
        }
    }

    /**
     * Logging for cart operations (clear cart)
     * Логирование операций с корзиной (очистка корзины)
     */
    @AfterReturning("execution(* de.ait.g_75_shop.service.interfaces.CustomerService.clearCart(..))")
    public void logCartCleared(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("🛒 ОЧИСТКА КОРЗИНЫ: Покупатель ID={}", args[0]);
        }
    }

    /**
     * Logging for cart total cost queries
     * Логирование запросов общей стоимости корзины
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.interfaces.CustomerService.getCustomerCartTotalCost(..))",
            returning = "result")
    public void logCartTotalCost(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("💰 СТОИМОСТЬ КОРЗИНЫ: Покупатель ID={}, Общая стоимость={}",
                    args[0], result);
        }
    }

    /**
     * Logging for cart average price queries
     * Логирование запросов средней цены в корзине
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.interfaces.CustomerService.getCustomerCartAveragePrice(..))",
            returning = "result")
    public void logCartAveragePrice(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("📊 СРЕДНЯЯ ЦЕНА В КОРЗИНЕ: Покупатель ID={}, Средняя цена={}",
                    args[0], result);
        }
    }

    /**
     * Logging for methods returning statistics (product count)
     * Логирование для методов, возвращающих статистику (количество товаров)
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.*.getAllActiveProductsCount(..))",
            returning = "result")
    public void logProductsCount(Object result) {
        logger.info("📊 СТАТИСТИКА: Всего активных товаров: {}", result);
    }

    /**
     * Logging for methods returning statistics (customer count)
     * Логирование для методов, возвращающих статистику (количество покупателей)
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.*.getAllActiveCustomersCount(..))",
            returning = "result")
    public void logCustomersCount(Object result) {
        logger.info("📊 СТАТИСТИКА: Всего активных покупателей: {}", result);
    }

    /**
     * Logging for methods returning statistics (total product cost)
     * Логирование для методов, возвращающих статистику (общая стоимость товаров)
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.interfaces.ProductService.getAllActiveProductsTotalCost(..))",
            returning = "result")
    public void logProductsTotalCost(Object result) {
        logger.info("📊 СТАТИСТИКА: Общая стоимость всех товаров: {}", result);
    }

    /**
     * Logging for methods returning statistics (average product price)
     * Логирование для методов, возвращающих статистику (средняя цена товаров)
     */
    @AfterReturning(pointcut = "execution(* de.ait.g_75_shop.service.interfaces.ProductService.getAllActiveProductsAveragePrice(..))",
            returning = "result")
    public void logProductsAveragePrice(Object result) {
        logger.info("📊 СТАТИСТИКА: Средняя цена товаров: {}", result);
    }

    // =============== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===============
    // =============== HELPER METHODS ===============

    /**
     * Determines operation type based on method name
     * Определяет тип операции на основе имени метода
     *
     * @param methodName name of the method / имя метода
     * @return operation type / тип операции
     */
    private String determineOperationType(String methodName) {
        if (methodName.startsWith("save")) return "CREATE";
        if (methodName.startsWith("update")) return "UPDATE";
        if (methodName.startsWith("delete") || methodName.startsWith("remove")) return "DELETE";
        if (methodName.startsWith("restore")) return "RESTORE";
        if (methodName.startsWith("add")) return "ADD";
        if (methodName.startsWith("clear")) return "CLEAR";
        if (methodName.startsWith("get") || methodName.startsWith("find")) return "READ";
        if (methodName.startsWith("count") || methodName.startsWith("is")) return "QUERY";
        return "OTHER";
    }

    /**
     * Masks sensitive data in arguments (for security)
     * Маскирует чувствительные данные в аргументах (для безопасности)
     *
     * @param args method arguments / аргументы метода
     * @return masked arguments / замаскированные аргументы
     */
    private Object[] maskSensitiveData(Object[] args) {
        // Here we can hide sensitive data (passwords, etc.)
        // In current project there is no sensitive data, but method kept for extension
        // Здесь можно скрыть чувствительные данные (пароли, и т.д.)
        // В текущем проекте чувствительных данных нет, но метод оставим для расширения
        return args;
    }

    /**
     * Formats result for logging based on its type
     * Форматирует результат для логирования в зависимости от его типа
     *
     * @param result method result / результат метода
     * @return formatted string / форматированная строка
     */
    private String formatResultForLogging(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) result;
            return "Collection(size=" + collection.size() + ")";
        }
        if (result instanceof Optional<?>) {
            Optional<?> optional = (Optional<?>) result;
            return optional.isPresent() ? "Optional[" + optional.get() + "]" : "Optional.empty";
        }
        return result.toString();
    }

    /**
     * Creates business event message for logging
     * Создает сообщение о бизнес-событии для логирования
     *
     * @param className class name / имя класса
     * @param methodName method name / имя метода
     * @param args method arguments / аргументы метода
     * @param result method result / результат метода
     * @return business event message / сообщение о бизнес-событии
     */
    private String createBusinessEventMessage(String className, String methodName,
                                              Object[] args, Object result) {
        StringBuilder message = new StringBuilder();

        switch (className) {
            case "ProductServiceImpl":
                message.append("Товар: ");
                break;
            case "CustomerServiceImpl":
                message.append("Покупатель: ");
                break;
            default:
                message.append(className).append(": ");
        }

        message.append(extractMethodDescription(methodName, args, result));
        return message.toString();
    }

    /**
     * Extracts human-readable description from method name and parameters
     * Извлекает читаемое описание из имени метода и параметров
     *
     * @param methodName method name / имя метода
     * @param args method arguments / аргументы метода
     * @param result method result / результат метода
     * @return human-readable description / читаемое описание
     */
    private String extractMethodDescription(String methodName, Object[] args, Object result) {
        if (methodName.startsWith("save")) {
            return "Создан новый объект: " + result;
        }
        if (methodName.startsWith("update")) {
            return String.format("Обновлен объект с ID=%s. Новые данные: %s",
                    args.length > 0 ? args[0] : "unknown",
                    args.length > 1 ? args[1] : "");
        }
        if (methodName.startsWith("delete")) {
            return String.format("Удален (soft delete) объект с ID=%s",
                    args.length > 0 ? args[0] : "unknown");
        }
        if (methodName.startsWith("restore")) {
            return String.format("Восстановлен объект с ID=%s",
                    args.length > 0 ? args[0] : "unknown");
        }
        if (methodName.contains("Cart")) {
            return describeCartOperation(methodName, args, result);
        }
        return methodName;
    }

    /**
     * Describes cart operation for business event logging
     * Описывает операцию с корзиной для логирования бизнес-событий
     *
     * @param methodName method name / имя метода
     * @param args method arguments / аргументы метода
     * @param result method result / результат метода
     * @return cart operation description / описание операции с корзиной
     */
    private String describeCartOperation(String methodName, Object[] args, Object result) {
        if (methodName.contains("addProductToCart")) {
            return String.format("Товар ID=%s добавлен в корзину покупателя ID=%s (количество: %s)",
                    args.length > 1 ? args[1] : "unknown",
                    args.length > 0 ? args[0] : "unknown",
                    args.length > 2 ? args[2] : "1");
        }
        if (methodName.contains("removeProductFromCart")) {
            return String.format("Товар ID=%s удален из корзины покупателя ID=%s",
                    args.length > 1 ? args[1] : "unknown",
                    args.length > 0 ? args[0] : "unknown");
        }
        if (methodName.contains("clearCart")) {
            return String.format("Корзина покупателя ID=%s полностью очищена",
                    args.length > 0 ? args[0] : "unknown");
        }
        if (methodName.contains("getCustomerCartTotalCost")) {
            return String.format("Запрошена стоимость корзины покупателя ID=%s. Стоимость: %s",
                    args.length > 0 ? args[0] : "unknown", result);
        }
        return methodName;
    }
}
