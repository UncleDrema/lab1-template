use axum::{
    Router,
    routing::{get, post, patch, delete},
    http::StatusCode,
    Json
};
use sqlx::postgres::PgPoolOptions;
use std::env;
use serde::{Deserialize, Serialize};
use axum::{extract::{State, Path}, response::IntoResponse};
use dotenv::dotenv;
use tracing::{info, error};

async fn health_check() -> &'static  str {
    "OK"
}

#[derive(Serialize, Deserialize, Debug, sqlx::FromRow)]
pub struct Person {
    pub id: i32,
    pub name: String,
    pub age: i32,
    pub address: String,
    pub work: String,
}

// Получить всех персон
async fn get_persons(State(pool): State<sqlx::PgPool>) -> impl IntoResponse {
    info!("Вызван GET /persons");
    let persons = sqlx::query_as::<_, Person>("SELECT id, name, age, address, work FROM person")
        .fetch_all(&pool)
        .await
        .unwrap_or_default();
    (StatusCode::OK, Json(persons))
}

// Получить по id
async fn get_person(State(pool): State<sqlx::PgPool>, Path(id): Path<i32>) -> impl IntoResponse {
    info!("Вызван GET /persons/{}", id);
    let person = sqlx::query_as::<_, Person>("SELECT id, name, age, address, work FROM person WHERE id = $1")
        .bind(id)
        .fetch_optional(&pool)
        .await;
    match person {
        Ok(Some(p)) => (StatusCode::OK, Json(p)).into_response(),
        Ok(None) => (StatusCode::NOT_FOUND, "Not found").into_response(),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, "DB error").into_response(),
    }
}

#[derive(Deserialize)]
pub struct NewPerson {
    pub name: String,
    pub age: i32,
    pub address: String,
    pub work: String,
}

// Создать
async fn create_person(State(pool): State<sqlx::PgPool>, Json(input): Json<NewPerson>) -> impl IntoResponse {
    info!("Вызван POST /persons: name={}, age={}, address={}, work={}", input.name, input.age, input.address, input.work);
    let rec = sqlx::query_as::<_, Person>(
        "INSERT INTO person (name, age, address, work) VALUES ($1, $2, $3, $4) RETURNING id, name, age, address, work"
    )
    .bind(&input.name)
    .bind(input.age)
    .bind(&input.address)
    .bind(&input.work)
    .fetch_one(&pool)
    .await;
    match rec {
        Ok(person) => (StatusCode::CREATED, Json(person)).into_response(),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, "DB error").into_response(),
    }
}

// Обновить
async fn update_person(State(pool): State<sqlx::PgPool>, Path(id): Path<i32>, Json(input): Json<NewPerson>) -> impl IntoResponse {
    info!("Вызван PATCH /persons/{}: name={}, age={}, address={}, work={}", id, input.name, input.age, input.address, input.work);
    let rec = sqlx::query_as::<_, Person>(
        "UPDATE person SET name = $1, age = $2, address = $3, work = $4 WHERE id = $5 RETURNING id, name, age, address, work"
    )
    .bind(&input.name)
    .bind(input.age)
    .bind(&input.address)
    .bind(&input.work)
    .bind(id)
    .fetch_optional(&pool)
    .await;
    match rec {
        Ok(Some(person)) => (StatusCode::OK, Json(person)).into_response(),
        Ok(None) => (StatusCode::NOT_FOUND, "Not found").into_response(),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, "DB error").into_response(),
    }
}

// Удалить
async fn delete_person(State(pool): State<sqlx::PgPool>, Path(id): Path<i32>) -> impl IntoResponse {
    info!("Вызван DELETE /persons/{}", id);
    let res = sqlx::query("DELETE FROM person WHERE id = $1")
        .bind(id)
        .execute(&pool)
        .await;
    match res {
        Ok(r) if r.rows_affected() > 0 => (StatusCode::NO_CONTENT, ()).into_response(),
        Ok(_) => (StatusCode::NOT_FOUND, "Not found").into_response(),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, "DB error").into_response(),
    }
}

#[tokio::main]
async fn main() {
    println!("Starting application...");
    // Инициализация логирования
    tracing_subscriber::fmt::init();
    info!("Запуск приложения...");
    // Загрузка переменных окружения
    dotenv().ok();
    let database_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set");
    info!("Подключение к базе данных: {}", database_url);
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&database_url)
        .await
        .expect("Не удалось подключиться к базе данных");
    info!("База данных подключена");

    let app = Router::new()
        .route("/health", get(health_check))
        .route("/persons", get(get_persons).post(create_person))
        .route("/persons/{id}", get(get_person).patch(update_person).delete(delete_person))
        .with_state(pool);

    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}
