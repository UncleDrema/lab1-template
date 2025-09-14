# Этап 1: Сборка на Alpine с musl
FROM rust:1.89-alpine AS builder

WORKDIR /app

RUN apk add --no-cache musl-dev pkgconfig openssl-dev

# Устанавливаем musl target для Rust
RUN rustup target add x86_64-unknown-linux-musl

COPY Cargo.toml Cargo.lock ./
RUN mkdir src && echo "fn main() {}" > src/main.rs
RUN cargo build --release --target x86_64-unknown-linux-musl
RUN rm -rf src

COPY . .
RUN cargo build --release --target x86_64-unknown-linux-musl

# Этап 2: Минимальный запуск на Alpine
FROM alpine:latest AS final

WORKDIR /app

RUN apk add --no-cache libpq

COPY --from=builder /app/target/x86_64-unknown-linux-musl/release/lab1 /app/lab1

EXPOSE 3000

CMD ["./lab1"]