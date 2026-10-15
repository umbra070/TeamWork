package org.skypro.teamwork.service;

import org.springframework.stereotype.Service;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.skypro.teamwork.dto.Recommendation;
import org.skypro.teamwork.dto.RecommendationResponse;
import org.skypro.teamwork.repos.RecommendationsRepository;
import org.skypro.teamwork.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;

@Service
public class TelegramBotService {
    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    private final TelegramBot bot;
    private final RecommendationService recommendationService;
    private final RecommendationsRepository recommendationsRepository;

    public TelegramBotService(
            @Value("${telegram.bot.token}") String token,
            RecommendationService recommendationService,
            RecommendationsRepository recommendationsRepository) {
        this.bot = new TelegramBot(token);
        this.recommendationService = recommendationService;
        this.recommendationsRepository = recommendationsRepository;
    }

    @PostConstruct
    public void start() {
        bot.setUpdatesListener(this::onUpdate, e -> log.error("Bot error", e));
        log.info("Telegram bot started");
    }

    private int onUpdate(List<Update> updates) {
        for (Update update : updates) {
            try {
                handle(update);
            } catch (Exception e) {
                log.error("Error handling update", e);
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handle(Update update) {
        if (update.message() == null || update.message().text() == null) return;
        long chatId = update.message().chat().id();
        String text = update.message().text().trim();

        if (text.startsWith("/recommend")) {
            handleRecommend(chatId, text);
        } else {
            sendHelp(chatId);
        }
    }

    private void handleRecommend(long chatId, String text) {
        String arg = text.replaceFirst("/recommend", "").trim();
        if (arg.isBlank()) {
            bot.execute(new SendMessage(chatId,
                    "Использование: /recommend Имя Фамилия"));
            return;
        }

        String[] parts = arg.split("\\s+", 2);
        List<UUID> found = (parts.length == 2)
                ? recommendationsRepository.findUserIdsByFullName(parts[0], parts[1])
                : recommendationsRepository.findUserIdsByName(parts[0]);

        if (found.size() != 1) {
            bot.execute(new SendMessage(chatId, "Пользователь не найден"));
            return;
        }

        RecommendationResponse response =
                recommendationService.getRecommendations(found.get(0));

        StringBuilder sb = new StringBuilder();
        sb.append("Здравствуйте, ").append(arg).append("!\n");
        if (response.getRecommendations().isEmpty()) {
            sb.append("К сожалению, новых продуктов для вас пока нет.");
        } else {
            sb.append("Новые продукты для вас:\n\n");
            for (Recommendation r : response.getRecommendations()) {
                sb.append("• ").append(r.getName()).append("\n")
                        .append(r.getText()).append("\n\n");
            }
        }
        bot.execute(new SendMessage(chatId, sb.toString()));
    }

    private void sendHelp(long chatId) {
        bot.execute(new SendMessage(chatId, """
                Привет! Я бот банка «Стар».
                Помогу подобрать банковские продукты.

                Команды:
                /recommend Имя Фамилия — получить рекомендации
                """));
    }
}
