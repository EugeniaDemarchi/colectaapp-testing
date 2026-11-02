const ars = new Intl.NumberFormat('es-AR', {
  style: 'currency',
  currency: 'ARS',
  maximumFractionDigits: 0,
})

/** 7450 → "$ 7.450". Usar siempre junto con la clase `tabular-nums`. */
export function formatearARS(monto: number): string {
  return ars.format(monto)
}

/**
 * "2026-07-03" → "03/07"
 *
 * No se usa Intl.DateTimeFormat: con los datos de idioma actuales (CLDR 48),
 * en español ignora `2-digit` para día/mes y devuelve "3/7". Además, el
 * resultado dependía de la versión del navegador. Se arma a mano desde el
 * texto ISO, que ya viene con dos dígitos y no pasa por zonas horarias.
 */
export function formatearFechaCorta(fechaISO: string): string {
  const [, mes, dia] = fechaISO.split('-')
  return `${dia}/${mes}`
}
