# java-explore-with-me

#Ссылка на PR
https://github.com/dimavakin/java-explore-with-me/pull/3



Template repository for ExploreWithMe project.
Функциональность "Комментарии"
Добавлена возможность оставлять комментарии к событиям
Реализовано управление своими комментариями
Поддержана интеграция со статистикой просмотров
Особенности:
- Комментировать могут только авторизованные пользователи
- Пользователи могут редактировать и удалять только свои комментарии
- Статистика просмотров комментариев интегрирована с сервисом статистики
- Поддержка пагинации при получении списка комментариев

Public:
- Получение комментариев к событию с пагинацией: GET /comments/{eventId}

Private:
- Получение своего комментария: GET /users/{userId}/comments/{commentId}
- Добавление нового комментария: POST /users/{userId}/events/{eventId}/comments
- Редактирование комментария: PATCH /users/{userId}/comments/{commentId}
- Удаление комментария: DELETE /users/{userId}/comments/{commentId}