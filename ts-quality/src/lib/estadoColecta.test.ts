import { describe, expect, it } from 'vitest'
import {
  type Aporte,
  type Colecta,
  estadoParaParticipante,
  estadoParaRecaudador,
  estaVencida,
} from './estadoColecta'
// Tests de CARACTERIZACIÓN: comparan la versión nueva con la heredada,
// para demostrar que la refactorización no cambió el comportamiento.
import {
  textoEstadoParticipante,
  textoEstadoRecaudador,
} from '../../auditoria/legacy/estadoColecta.legacy'

const ANTES_DEL_LIMITE = new Date('2026-07-01T10:00:00')
const DESPUES_DEL_LIMITE = new Date('2026-07-10T10:00:00')

function colecta(cambios: Partial<Colecta> = {}): Colecta {
  return {
    estado: 'en_recaudacion',
    fechaLimite: '2026-07-03',
    permiteTardios: true,
    montoObjetivo: 120000,
    recaudadorId: 'padre-recaudador',
    ...cambios,
  }
}

// formatearARS separa "$" y número con un espacio no separable (U+00A0);
// el código heredado usaba un espacio común. Es la única diferencia aceptada.
const normalizar = (texto: string) => texto.replace(/\u00a0/g, ' ')

describe('estadoParaParticipante: mismo resultado que el código heredado', () => {
  const casos: [string, Colecta, Aporte | null, string, Date][] = [
    [
      'es el recaudador',
      colecta(),
      { monto: 7450, pagado: false },
      'padre-recaudador',
      ANTES_DEL_LIMITE,
    ],
    ['no participa', colecta(), null, 'padre-1', ANTES_DEL_LIMITE],
    ['ya pagó', colecta(), { monto: 7450, pagado: true }, 'padre-1', ANTES_DEL_LIMITE],
    [
      'pendiente en término',
      colecta(),
      { monto: 7450, pagado: false },
      'padre-1',
      ANTES_DEL_LIMITE,
    ],
    [
      'vencido con recargo',
      colecta(),
      { monto: 7450, pagado: false },
      'padre-1',
      DESPUES_DEL_LIMITE,
    ],
    [
      'vencido sin tardíos',
      colecta({ permiteTardios: false }),
      { monto: 7450, pagado: false },
      'padre-1',
      DESPUES_DEL_LIMITE,
    ],
    [
      'cerrada y pagó',
      colecta({ estado: 'cerrada' }),
      { monto: 7450, pagado: true },
      'padre-1',
      DESPUES_DEL_LIMITE,
    ],
    ['cerrada sin pagar', colecta({ estado: 'cerrada' }), null, 'padre-1', DESPUES_DEL_LIMITE],
    ['entregada', colecta({ estado: 'entregada' }), null, 'padre-1', DESPUES_DEL_LIMITE],
  ]

  it.each(casos)('%s', (_nombre, c, aporte, usuarioId, hoy) => {
    // Arrange: los datos vienen de la tabla de casos
    // Act
    const nuevo = estadoParaParticipante(c, aporte, usuarioId, hoy)
    const viejo = textoEstadoParticipante(c, aporte, usuarioId, hoy)
    // Assert
    expect({ ...nuevo, texto: normalizar(nuevo.texto) }).toEqual(viejo)
  })
})

describe('estadoParaRecaudador: mismo resultado que el código heredado', () => {
  const pago = (monto: number): Aporte => ({ monto, pagado: true })
  const debe = (monto: number): Aporte => ({ monto, pagado: false })

  const casos: [string, Colecta, Aporte[], Date][] = [
    ['sin participantes', colecta(), [], ANTES_DEL_LIMITE],
    ['todos pagaron', colecta(), [pago(7450), pago(7450)], ANTES_DEL_LIMITE],
    ['en curso', colecta(), [pago(7450), debe(7450), debe(7450)], ANTES_DEL_LIMITE],
    ['vencida con deudas', colecta(), [pago(7450), debe(7450)], DESPUES_DEL_LIMITE],
    ['cerrada', colecta({ estado: 'cerrada' }), [pago(7450)], DESPUES_DEL_LIMITE],
    ['entregada', colecta({ estado: 'entregada' }), [pago(7450)], DESPUES_DEL_LIMITE],
  ]

  it.each(casos)('%s', (_nombre, c, aportes, hoy) => {
    const nuevo = estadoParaRecaudador(c, aportes, hoy)
    const viejo = textoEstadoRecaudador(c, aportes, hoy)
    expect({ ...nuevo, texto: normalizar(nuevo.texto) }).toEqual(viejo)
  })
})

describe('estaVencida', () => {
  it('el mismo día límite todavía no está vencida', () => {
    expect(estaVencida('2026-07-03', new Date('2026-07-03T09:00:00'))).toBe(false)
  })

  it('al día siguiente sí', () => {
    expect(estaVencida('2026-07-03', new Date('2026-07-04T09:00:00'))).toBe(true)
  })
})
