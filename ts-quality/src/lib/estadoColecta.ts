import { formatearARS, formatearFechaCorta } from './formato'

// ---------------------------------------------------------------------
// Estado visible de una colecta (texto + color de la pill).
// Versión refactorizada de auditoria/legacy/estadoColecta.legacy.ts
// ---------------------------------------------------------------------

export type EstadoColecta = 'en_recaudacion' | 'cerrada' | 'entregada'
export type ColorPill = 'verde' | 'amarillo' | 'rojo' | 'gris'

export interface Colecta {
  estado: EstadoColecta
  fechaLimite: string // ISO "2026-07-03"
  permiteTardios: boolean
  montoObjetivo: number
  recaudadorId: string
}

export interface Aporte {
  monto: number
  pagado: boolean
}

export interface EstadoVisible {
  texto: string
  color: ColorPill
}

/** Recargo por aporte fuera de término (antes era un 1.1 "mágico" en el medio del código). */
export const RECARGO_TARDIO = 1.1

/** Estados finales: igual para todas las vistas → una sola tabla en vez de if/else repetidos. */
const ESTADOS_FINALES: Record<Exclude<EstadoColecta, 'en_recaudacion'>, EstadoVisible> = {
  cerrada: { texto: 'Colecta cerrada', color: 'gris' },
  entregada: { texto: 'Regalo entregado', color: 'verde' },
}

/** true si `hoy` ya pasó el día límite (antes se calculaba en dos funciones distintas). */
export function estaVencida(fechaLimite: string, hoy: Date): boolean {
  return hoy > new Date(`${fechaLimite}T12:00:00`)
}

// ----------------------------- PARTICIPANTE ---------------------------

export function estadoParaParticipante(
  colecta: Colecta,
  aporte: Aporte | null,
  usuarioId: string,
  hoy: Date,
): EstadoVisible {
  if (colecta.estado === 'cerrada' && aporte?.pagado) {
    return { texto: 'Colecta cerrada: aportaste', color: 'gris' }
  }
  if (colecta.estado !== 'en_recaudacion') return ESTADOS_FINALES[colecta.estado]
  if (colecta.recaudadorId === usuarioId) {
    return { texto: 'Sos el recaudador: no aportás a esta colecta', color: 'gris' }
  }
  if (!aporte) return { texto: 'No participás de esta colecta', color: 'gris' }
  if (aporte.pagado) return { texto: 'Ya aportaste', color: 'verde' }
  return aportePendiente(colecta, aporte, hoy)
}

function aportePendiente(colecta: Colecta, aporte: Aporte, hoy: Date): EstadoVisible {
  if (!estaVencida(colecta.fechaLimite, hoy)) {
    const monto = formatearARS(aporte.monto)
    const fecha = formatearFechaCorta(colecta.fechaLimite)
    return { texto: `Te falta aportar ${monto} hasta el ${fecha}`, color: 'amarillo' }
  }
  if (!colecta.permiteTardios) return { texto: 'La colecta ya no recibe aportes', color: 'gris' }
  const conRecargo = formatearARS(aporte.monto * RECARGO_TARDIO)
  return { texto: `Aporte vencido: ${conRecargo} (con recargo)`, color: 'rojo' }
}

// ----------------------------- RECAUDADOR -----------------------------

export function estadoParaRecaudador(
  colecta: Colecta,
  aportes: Aporte[],
  hoy: Date,
): EstadoVisible {
  if (colecta.estado !== 'en_recaudacion') return ESTADOS_FINALES[colecta.estado]
  if (aportes.length === 0) return { texto: 'Todavía no hay participantes', color: 'gris' }

  const pagados = aportes.filter((a) => a.pagado)
  const total = formatearARS(pagados.reduce((suma, a) => suma + a.monto, 0))
  const faltan = aportes.length - pagados.length

  if (faltan === 0) return { texto: `Ya aportaron todos: ${total}`, color: 'verde' }
  if (estaVencida(colecta.fechaLimite, hoy)) {
    return { texto: `Faltan ${faltan} aportes y la fecha venció`, color: 'rojo' }
  }
  const objetivo = formatearARS(colecta.montoObjetivo)
  const avance = `${pagados.length}/${aportes.length}`
  return { texto: `Recaudado ${total} de ${objetivo} (${avance})`, color: 'amarillo' }
}
