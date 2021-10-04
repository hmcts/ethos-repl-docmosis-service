package uk.gov.hmcts.ethos.replacement.docmosis.config;

import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.IQueueClient;
import com.microsoft.azure.servicebus.ISessionHandler;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.SessionHandlerOptions;
import com.microsoft.azure.servicebus.TransactionContext;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * {@link IQueueClient} implementation for use during development only.
 * To use you would need to add
 * <pre>{@code
 * @Component("create-updates-send-client")
 * @Profile("dev")
 * }</pre>
 */
@SuppressWarnings("ALL")
public class DevQueueClient implements IQueueClient {

    @Override
    public ReceiveMode getReceiveMode() {
        return null;
    }

    @Override
    public String getQueueName() {
        return null;
    }

    @Override
    public void registerMessageHandler(IMessageHandler handler) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void registerMessageHandler(IMessageHandler handler, ExecutorService executorService)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void registerMessageHandler(IMessageHandler handler, MessageHandlerOptions handlerOptions)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void registerMessageHandler(IMessageHandler handler, MessageHandlerOptions handlerOptions,
                                       ExecutorService executorService)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void registerSessionHandler(ISessionHandler handler) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void registerSessionHandler(ISessionHandler handler, ExecutorService executorService)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void registerSessionHandler(ISessionHandler handler, SessionHandlerOptions handlerOptions)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void registerSessionHandler(ISessionHandler handler, SessionHandlerOptions handlerOptions,
                                       ExecutorService executorService)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void abandon(UUID lockToken) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void abandon(UUID lockToken, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void abandon(UUID lockToken, Map<String, Object> propertiesToModify)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void abandon(UUID lockToken, Map<String, Object> propertiesToModify, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public CompletableFuture<Void> abandonAsync(UUID lockToken) {
        return null;
    }

    @Override
    public CompletableFuture<Void> abandonAsync(UUID lockToken, TransactionContext transaction) {
        return null;
    }

    @Override
    public CompletableFuture<Void> abandonAsync(UUID lockToken, Map<String, Object> propertiesToModify) {
        return null;
    }

    @Override
    public CompletableFuture<Void> abandonAsync(UUID lockToken, Map<String, Object> propertiesToModify,
                                                TransactionContext transaction) {
        return null;
    }

    @Override
    public void complete(UUID lockToken) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void complete(UUID lockToken, TransactionContext transaction) throws InterruptedException,
            ServiceBusException {
        // No implementation required
    }

    @Override
    public CompletableFuture<Void> completeAsync(UUID lockToken) {
        return null;
    }

    @Override
    public CompletableFuture<Void> completeAsync(UUID lockToken, TransactionContext transaction) {
        return null;
    }

    @Override
    public void deadLetter(UUID lockToken) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void deadLetter(UUID lockToken, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void deadLetter(UUID lockToken, Map<String, Object> propertiesToModify)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void deadLetter(UUID lockToken, Map<String, Object> propertiesToModify, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void deadLetter(UUID lockToken, String deadLetterReason, String deadLetterErrorDescription)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void deadLetter(UUID lockToken, String deadLetterReason, String deadLetterErrorDescription,
                           TransactionContext transaction) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void deadLetter(UUID lockToken, String deadLetterReason, String deadLetterErrorDescription,
                           Map<String, Object> propertiesToModify) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void deadLetter(UUID lockToken, String deadLetterReason, String deadLetterErrorDescription,
                           Map<String, Object> propertiesToModify, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken, TransactionContext transaction) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken, Map<String, Object> propertiesToModify) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken, Map<String, Object> propertiesToModify,
                                                   TransactionContext transaction) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken, String deadLetterReason,
                                                   String deadLetterErrorDescription) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken, String deadLetterReason,
                                                   String deadLetterErrorDescription, TransactionContext transaction) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken, String deadLetterReason,
                                                   String deadLetterErrorDescription,
                                                   Map<String, Object> propertiesToModify) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deadLetterAsync(UUID lockToken, String deadLetterReason,
                                                   String deadLetterErrorDescription,
                                                   Map<String, Object> propertiesToModify,
                                                   TransactionContext transaction) {
        return null;
    }

    @Override
    public int getPrefetchCount() {
        return 0;
    }

    @Override
    public void setPrefetchCount(int prefetchCount) throws ServiceBusException {
        // No implementation required
    }

    @Override
    public void send(IMessage message) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void send(IMessage message, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void sendBatch(Collection<? extends IMessage> messages) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public void sendBatch(Collection<? extends IMessage> messages, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public CompletableFuture<Void> sendAsync(IMessage message) {
        return null;
    }

    @Override
    public CompletableFuture<Void> sendAsync(IMessage message, TransactionContext transaction) {
        return null;
    }

    @Override
    public CompletableFuture<Void> sendBatchAsync(Collection<? extends IMessage> messages) {
        return null;
    }

    @Override
    public CompletableFuture<Void> sendBatchAsync(Collection<? extends IMessage> messages,
                                                  TransactionContext transaction) {
        return null;
    }

    @Override
    public CompletableFuture<Long> scheduleMessageAsync(IMessage message, Instant scheduledEnqueueTimeUtc) {
        return null;
    }

    @Override
    public CompletableFuture<Long> scheduleMessageAsync(IMessage message, Instant scheduledEnqueueTimeUtc,
                                                        TransactionContext transaction) {
        return null;
    }

    @Override
    public CompletableFuture<Void> cancelScheduledMessageAsync(long sequenceNumber) {
        return null;
    }

    @Override
    public long scheduleMessage(IMessage message, Instant scheduledEnqueueTimeUtc)
            throws InterruptedException, ServiceBusException {
        return 0;
    }

    @Override
    public long scheduleMessage(IMessage message, Instant scheduledEnqueueTimeUtc, TransactionContext transaction)
            throws InterruptedException, ServiceBusException {
        return 0;
    }

    @Override
    public void cancelScheduledMessage(long sequenceNumber) throws InterruptedException, ServiceBusException {
        // No implementation required
    }

    @Override
    public String getEntityPath() {
        return null;
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        return null;
    }

    @Override
    public void close() throws ServiceBusException {
        // No implementation required
    }
}
