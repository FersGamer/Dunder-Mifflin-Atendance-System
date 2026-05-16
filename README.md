# Dunder Mifflin Attendance System
Sistema de control de asistencias con QR.

## Estructura
- `/web` — Panel de administrador (Vue + Tailwind + Supabase)
- `/mobile` — App móvil Android (Java + Supabase)

## Configuración Web
1. `cd web`
2. `npm install`
3. Crear `.env` con las keys de Supabase
4. `npm run dev`

## Configuración Mobile
1. Abrir `/mobile` en Android Studio
2. Agregar keys de Supabase en `gradle.properties`
3. Correr el proyecto