// =====================================================================
// CÓDIGO HEREDADO DE BAJA CALIDAD — escrito A PROPÓSITO para el TP 3.
// Arma el texto y el color de la "pill" de estado que ve cada usuario
// en una colecta de ColectaApp. Funciona, pero es difícil de mantener.
// Esta carpeta está excluida del hook (ESLint/Prettier) a propósito.
// =====================================================================

// Vista del PARTICIPANTE (padre que tiene que aportar)
export function textoEstadoParticipante(colecta: any, aporte: any, usuarioId: any, hoy: Date): any {
  let texto = ''
  let color = ''
  if (colecta != null) {
    if (colecta.estado == 'en_recaudacion') {
      if (colecta.recaudadorId == usuarioId) {
        texto = 'Sos el recaudador: no aportás a esta colecta'
        color = 'gris'
      } else if (aporte != null) {
        if (aporte.pagado == true) {
          texto = 'Ya aportaste'
          color = 'verde'
        } else {
          const vence = new Date(colecta.fechaLimite + 'T12:00:00')
          if (hoy > vence) {
            if (colecta.permiteTardios == true) {
              let monto = aporte.monto * 1.1
              const m = '$ ' + Math.round(monto).toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
              texto = 'Aporte vencido: ' + m + ' (con recargo)'
              color = 'rojo'
            } else {
              texto = 'La colecta ya no recibe aportes'
              color = 'gris'
            }
          } else {
            const m = '$ ' + Math.round(aporte.monto).toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
            const d = colecta.fechaLimite.split('-')
            texto = 'Te falta aportar ' + m + ' hasta el ' + d[2] + '/' + d[1]
            color = 'amarillo'
          }
        }
      } else {
        texto = 'No participás de esta colecta'
        color = 'gris'
      }
    } else if (colecta.estado == 'cerrada') {
      if (aporte != null && aporte.pagado == true) {
        texto = 'Colecta cerrada: aportaste'
      } else {
        texto = 'Colecta cerrada'
      }
      color = 'gris'
    } else if (colecta.estado == 'entregada') {
      texto = 'Regalo entregado'
      color = 'verde'
    }
  }
  return { texto: texto, color: color }
}

// Vista del RECAUDADOR (padre que junta la plata)
export function textoEstadoRecaudador(colecta: any, aportes: any, hoy: Date): any {
  let texto = ''
  let color = ''
  if (colecta != null) {
    if (colecta.estado == 'en_recaudacion') {
      if (aportes == null || aportes.length == 0) {
        return { texto: 'Todavía no hay participantes', color: 'gris' }
      }
      let total = 0
      let pagados = 0
      for (let i = 0; i < aportes.length; i++) {
        if (aportes[i].pagado == true) {
          total = total + aportes[i].monto
          pagados++
        }
      }
      const vence = new Date(colecta.fechaLimite + 'T12:00:00')
      if (pagados == aportes.length) {
        const m = '$ ' + Math.round(total).toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
        texto = 'Ya aportaron todos: ' + m
        color = 'verde'
      } else {
        if (hoy > vence) {
          texto = 'Faltan ' + (aportes.length - pagados) + ' aportes y la fecha venció'
          color = 'rojo'
        } else {
          const m = '$ ' + Math.round(total).toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
          const o = '$ ' + Math.round(colecta.montoObjetivo).toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
          texto = 'Recaudado ' + m + ' de ' + o + ' (' + pagados + '/' + aportes.length + ')'
          color = 'amarillo'
        }
      }
    } else if (colecta.estado == 'cerrada') {
      texto = 'Colecta cerrada'
      color = 'gris'
    } else if (colecta.estado == 'entregada') {
      texto = 'Regalo entregado'
      color = 'verde'
    }
  }
  return { texto: texto, color: color }
}
