# Pre-commit hook — cómo probarlo en tu compu (VS Code)

Proyecto de práctica: copia del frontend de ColectaApp. **No toca el repo del equipo.**

## 1. Preparar (una sola vez)

Abrí la carpeta `colectaapp-calidad` en VS Code y en la terminal (Ctrl + ñ) corré:

```bash
npm install
git init -b main
git config core.hooksPath .githooks
git add -A
git commit -m "Proyecto de práctica con Prettier, ESLint, Vitest y pre-commit hook"
```

La línea `git config core.hooksPath .githooks` le dice a Git: "antes de cada commit, ejecutá los scripts de la carpeta `.githooks`". En ese primer commit ya vas a ver correr los 3 controles en verde.

> Si VS Code abre la terminal en PowerShell no pasa nada: Git ejecuta el hook con su propio Bash (Git for Windows).

## 2. Ver al hook frenar un commit (evidencia del punto 1)

**A. Que lo frene el linter.** En `src/lib/formato.ts` agregá una línea al final:

```ts
const sinUsar = 1
```

Después: `git add -A` y `git commit -m "prueba linter"`. El commit se cancela con el error `'sinUsar' is assigned a value but never used`. Sacá la captura. Borrá la línea.

**B. Que lo frene un test.** En `src/lib/formato.ts` cambiá `return \`${dia}/${mes}\`` por `return \`${mes}/${dia}\``. Hacé `git add -A` y `git commit -m "prueba tests"`. El test que espera `"03/07"` recibe `"07/03"`, sale en rojo y el commit se cancela. Captura. Deshacé el cambio.

**C. Que lo frene el formatter.** En cualquier archivo de `src` cambiá comillas simples por dobles en una línea. Hacé el commit: Prettier lo marca. Arreglalo con `npm run format`.

Para confirmar que ninguno de los intentos fallidos quedó guardado: `git log --oneline`.

## 3. Comandos sueltos

| Comando | Qué hace |
|---|---|
| `npm run format` | Acomoda el formato de todo `src` |
| `npm run format:check` | Solo revisa el formato (lo que usa el hook) |
| `npm run lint` | ESLint |
| `npm test` | Tests unitarios |
| `npm run test:coverage` | Tests + cobertura (reporte HTML en `coverage/index.html`) |

La carpeta `evidencias/` tiene la salida real de consola de cuando lo armé y probé.

---

# Punto 2 — Auditoría estática

## Qué hay en la carpeta `auditoria/`

| Archivo | Qué es |
|---|---|
| `legacy/estadoColecta.legacy.ts` | Código **malo a propósito**: el texto y color de la pill de estado de una colecta, para el participante y para el recaudador. |
| `../src/lib/estadoColecta.ts` | La misma lógica, **refactorizada**. |
| `../src/lib/estadoColecta.test.ts` | Tests que comparan la versión nueva con la vieja caso por caso (tests de caracterización). |
| `dependencias-legacy/package.json` | Un `package.json` "heredado" con versiones viejas (axios 0.21.1, vite 4, react-router 6.3). |
| `dependencias-corregidas/package.json` | El mismo, con las versiones actuales. |
| `resultados/*.log` | La salida real de todas las mediciones, antes y después. |

## 1. Instalar la herramienta nueva y registrar el cambio

```
npm install
git add -A
git commit -m "Punto 2: auditoría estática y refactorización de estadoColecta"
```

El hook tiene que pasar en verde: la versión refactorizada cumple todas las reglas, incluidas las nuevas de complejidad. **Sacá captura.**

## 2. Correr las mediciones (una captura de cada una)

```
npm run auditoria:complejidad
```
Muestra la complejidad de cada función. Buscá las líneas `has a complexity of`: el código heredado da **12 y 11**, el refactorizado **8, 3 y 5**.

```
npm run auditoria:duplicacion
```
Busca código copiado y pegado. Los clones aparecen todos en el archivo `legacy`; en `src` no hay ninguno.

```
cd auditoria\dependencias-legacy
npm audit
cd ..\dependencias-corregidas
npm audit
cd ..\..
```
El heredado da **5 vulnerabilidades (2 altas)**; el corregido, **0**.

## 3. Ver a la regla nueva frenar código complejo (opcional)

Copiá `auditoria\legacy\estadoColecta.legacy.ts` dentro de `src\lib\`, hacé `git add -A` y `git commit -m "prueba complejidad"`. El hook lo frena por `complexity`, `max-depth` y `eqeqeq`. Captura, y después borrá la copia con `del src\lib\estadoColecta.legacy.ts`.
