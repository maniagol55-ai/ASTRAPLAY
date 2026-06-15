package com.michitv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.michitv.databinding.FragmentSettingsBinding
import com.michitv.util.PreferencesManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parentalEnabled = PreferencesManager.isParentalEnabled(requireContext())
        binding.switchParental.isChecked = parentalEnabled
        updateParentalStatus(parentalEnabled)

        binding.switchParental.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val pin = PreferencesManager.getParentalPin(requireContext())
                if (pin == null) {
                    showSetPinDialog { newPin ->
                        PreferencesManager.setParentalPin(requireContext(), newPin)
                        PreferencesManager.setParentalEnabled(requireContext(), true)
                        updateParentalStatus(true)
                        Toast.makeText(requireContext(), "✅ Control parental activado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    PreferencesManager.setParentalEnabled(requireContext(), true)
                    updateParentalStatus(true)
                }
            } else {
                val pin = PreferencesManager.getParentalPin(requireContext())
                if (pin != null) {
                    showVerifyPinDialog(pin) {
                        PreferencesManager.setParentalEnabled(requireContext(), false)
                        updateParentalStatus(false)
                        Toast.makeText(requireContext(), "Control parental desactivado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    PreferencesManager.setParentalEnabled(requireContext(), false)
                    updateParentalStatus(false)
                }
            }
        }

        binding.btnChangePin.setOnClickListener {
            val currentPin = PreferencesManager.getParentalPin(requireContext())
            if (currentPin != null) {
                showVerifyPinDialog(currentPin) {
                    showSetPinDialog { newPin ->
                        PreferencesManager.setParentalPin(requireContext(), newPin)
                        Toast.makeText(requireContext(), "✅ PIN actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                showSetPinDialog { newPin ->
                    PreferencesManager.setParentalPin(requireContext(), newPin)
                    Toast.makeText(requireContext(), "✅ PIN configurado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvVersion.text = "MichiTV v1.0"
        binding.tvUser.text = "Usuario: ${PreferencesManager.getLoggedUser(requireContext()) ?: "-"}"
    }

    private fun updateParentalStatus(enabled: Boolean) {
        binding.tvParentalStatus.text = if (enabled) "🔒 Control parental ACTIVADO" else "🔓 Control parental desactivado"
        binding.btnChangePin.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    private fun showSetPinDialog(onSet: (String) -> Unit) {
        val input = EditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "PIN de 4 dígitos"
            filters = arrayOf(android.text.InputFilter.LengthFilter(4))
        }
        AlertDialog.Builder(requireContext())
            .setTitle("🔒 Configurar PIN parental")
            .setMessage("Ingresa un PIN de 4 dígitos para proteger canales adultos")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val pin = input.text.toString()
                if (pin.length == 4) onSet(pin)
                else Toast.makeText(requireContext(), "El PIN debe tener 4 dígitos", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { _, _ -> binding.switchParental.isChecked = false }
            .show()
    }

    private fun showVerifyPinDialog(correctPin: String, onCorrect: () -> Unit) {
        val input = EditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "Ingresa tu PIN"
            filters = arrayOf(android.text.InputFilter.LengthFilter(4))
        }
        AlertDialog.Builder(requireContext())
            .setTitle("🔒 Verificar PIN")
            .setView(input)
            .setPositiveButton("Confirmar") { _, _ ->
                if (input.text.toString() == correctPin) onCorrect()
                else {
                    Toast.makeText(requireContext(), "PIN incorrecto", Toast.LENGTH_SHORT).show()
                    binding.switchParental.isChecked = true
                }
            }
            .setNegativeButton("Cancelar") { _, _ -> binding.switchParental.isChecked = true }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
