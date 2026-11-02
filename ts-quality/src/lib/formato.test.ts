import { describe, expect, it } from 'vitest'
import { formatearARS, formatearFechaCorta } from './formato'

// Intl separa el signo $ del número con un espacio no separable (U+00A0)
const NBSP = '\u00a0'

describe('formatearARS', () => {
  it('formatea un monto en pesos con separador de miles', () => {
    // Arrange
    const monto = 7450
    // Act
    const resultado = formatearARS(monto)
    // Assert
    expect(resultado).toBe(`$${NBSP}7.450`)
  })

  it('redondea los centavos, porque la app muestra pesos enteros', () => {
    expect(formatearARS(1499.99)).toBe(`$${NBSP}1.500`)
  })

  it('formatea el cero', () => {
    expect(formatearARS(0)).toBe(`$${NBSP}0`)
  })
})

describe('formatearFechaCorta', () => {
  it('convierte una fecha ISO a día/mes', () => {
    expect(formatearFechaCorta('2026-07-03')).toBe('03/07')
  })

  it('no corre la fecha un día por la zona horaria (borde: 1 de enero)', () => {
    expect(formatearFechaCorta('2026-01-01')).toBe('01/01')
  })
})
