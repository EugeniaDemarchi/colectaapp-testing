import { describe, expect, it } from 'vitest'
import { CONFETI, colorCumpleanero } from './confeti'

describe('colorCumpleanero', () => {
  it('devuelve siempre el mismo color para el mismo id', () => {
    // Arrange
    const id = '66f1a2b3c4d5e6f708192a3b'
    // Act
    const primera = colorCumpleanero(id)
    const segunda = colorCumpleanero(id)
    // Assert
    expect(segunda).toBe(primera)
  })

  it('siempre devuelve uno de los 5 colores de la paleta', () => {
    const ids = ['a', 'b', 'abc', '66f1a2b3c4d5e6f708192a3b', 'x'.repeat(500)]
    for (const id of ids) {
      expect(CONFETI).toContain(colorCumpleanero(id))
    }
  })

  it('con un id vacío no falla y devuelve el primer color', () => {
    expect(colorCumpleanero('')).toBe(CONFETI[0])
  })

  it('reparte colores distintos entre cumpleañeros distintos', () => {
    const ids = Array.from({ length: 50 }, (_, i) => `beneficiario-${i}`)
    const usados = new Set(ids.map((id) => colorCumpleanero(id).nombre))
    expect(usados.size).toBe(CONFETI.length)
  })
})
