package com.example.proyectofinal.ui.citas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.CitaDto

class CitasAdapter(
    private val onCitaClick: (CitaDto) -> Unit
) : ListAdapter<CitaDto, CitasAdapter.CitaViewHolder>(CitaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view, onCitaClick)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CitaViewHolder(
        itemView: View,
        private val onCitaClick: (CitaDto) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvPacienteNombre: TextView = itemView.findViewById(R.id.tvPacienteNombre)
        private val tvMedicoNombre: TextView = itemView.findViewById(R.id.tvMedicoNombre)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        private val tvMotivo: TextView = itemView.findViewById(R.id.tvMotivo)

        fun bind(cita: CitaDto) {
            tvPacienteNombre.text = cita.pacienteNombre ?: "Paciente ID: ${cita.pacienteId}"
            tvMedicoNombre.text = cita.medicoNombre ?: "Médico ID: ${cita.medicoId}"
            tvFecha.text = cita.fechaCita
            tvHora.text = "${cita.horaInicio} - ${cita.horaFin}"
            tvEstado.text = cita.estado

            if (cita.motivo.isNullOrEmpty()) {
                tvMotivo.visibility = View.GONE
            } else {
                tvMotivo.visibility = View.VISIBLE
                tvMotivo.text = cita.motivo
            }

            itemView.setOnClickListener {
                onCitaClick(cita)
            }
        }
    }

    private class CitaDiffCallback : DiffUtil.ItemCallback<CitaDto>() {
        override fun areItemsTheSame(oldItem: CitaDto, newItem: CitaDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CitaDto, newItem: CitaDto): Boolean {
            return oldItem == newItem
        }
    }
}
