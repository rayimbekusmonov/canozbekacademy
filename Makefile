.PHONY: help build up down restart logs clean dev prod

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build all Docker images
	docker-compose build

up: ## Start all services
	docker-compose up -d

down: ## Stop all services
	docker-compose down

restart: ## Restart all services
	docker-compose restart

logs: ## Show logs from all services
	docker-compose logs -f

logs-backend: ## Show backend logs
	docker-compose logs -f backend

logs-frontend: ## Show frontend logs
	docker-compose logs -f frontend

logs-postgres: ## Show postgres logs
	docker-compose logs -f postgres

clean: ## Remove all containers, volumes, and images
	docker-compose down -v --rmi all

dev: ## Start development environment
	@echo "Starting development environment..."
	docker-compose up -d postgres redis minio
	@echo "Waiting for services to be healthy..."
	@sleep 10
	@echo "Services are ready!"
	@echo ""
	@echo "PostgreSQL: localhost:5432"
	@echo "Redis: localhost:6379"
	@echo "MinIO Console: http://localhost:9001"
	@echo "MinIO API: http://localhost:9000"
	@echo ""
	@echo "Now run your backend and frontend locally!"

prod: ## Start production environment
	docker-compose -f docker-compose.prod.yml up -d

status: ## Show status of all services
	docker-compose ps

shell-backend: ## Open shell in backend container
	docker-compose exec backend sh

shell-postgres: ## Open PostgreSQL shell
	docker-compose exec postgres psql -U canozbek_user -d canozbek_academy

shell-redis: ## Open Redis CLI
	docker-compose exec redis redis-cli

backup-db: ## Backup database
	docker-compose exec postgres pg_dump -U canozbek_user canozbek_academy > backup_$(shell date +%Y%m%d_%H%M%S).sql

restore-db: ## Restore database (use: make restore-db FILE=backup.sql)
	docker-compose exec -T postgres psql -U canozbek_user -d canozbek_academy < $(FILE)

init: ## Initialize project (first time setup)
	@echo "🚀 Initializing Canozbek Academy..."
	@cp .env.example .env
	@echo "✅ Created .env file"
	@echo ""
	@echo "⚠️  IMPORTANT: Edit .env file with your configuration!"
	@echo "   - Set your Gmail credentials"
	@echo "   - Update JWT secret"
	@echo ""
	@echo "Then run: make up"