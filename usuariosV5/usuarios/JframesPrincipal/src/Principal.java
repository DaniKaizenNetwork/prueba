

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Principal extends JFrame{
    private JPanel JpanelPrincipal;
    private JLabel JLabelPNombre;
    private JTextField JtextFieldPNombre;
    private JLabel JLabelSNombre;
    private JTextField JtextFieldSNombre;
    private JLabel JLabelPApellido;
    private JTextField JtextFieldPApellido;
    private JLabel JLabelSApellido;
    private JTextField JtextFieldSApellido;
    private JLabel JLabelGenero;
    private JRadioButton masculinoRadioButton;
    private JRadioButton femeninoRadioButton;
    private JRadioButton noBinarioRadioButton;
    private JRadioButton lgbtiqRadioButton;
    private JLabel JLabelTipoI;
    private JComboBox JcomboBoxTipo;
    private JLabel JLabelNumeroI;
    private JTextField JtextFieldNumeroI;
    private JLabel JLabelDias;
    private JCheckBox lunesCheckBox;
    private JCheckBox martesCheckBox;
    private JCheckBox miercolesCheckBox;
    private JCheckBox juevesCheckBox;
    private JCheckBox viernesCheckBox;
    private JLabel JLabelVHora;
    private JTextField JtextFieldVHora;
    private JLabel JLabelTotHoras;
    private JTextField JtextFieldTHoras;
    private JLabel JLabelObserv;
    private JTextArea JtextAreaObserv;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JScrollPane JscrollTabla;
    private JTable tabla;
    private JTextField JTextFieldBusqueda;
    private JLabel JLabelBuscar;
    private JButton buscarTablaButton;
    private JButton JbuttonExportar;
    private JButton JbuttonPdf;
    private TableRowSorter <DefaultTableModel> filtro;


    public Principal(){
        //configuración del JFrame
        setTitle("Datos Personales");
        //tamaño de la ventana ancho y alto
        setSize(1000, 600);
        //agregar las opciones de minimizar, maximizar y cerrar a la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //agregar contenido al panel principal
        setContentPane(JpanelPrincipal);

        //Crear la configuración para la tabla
        String [] nomColumnas={"Primer Nombre", "Segundo Nombre", "Primer Apellido", "Segundo Apellido",
                                "Género", "Tipo ID", "Número ID","Días", "Valor Hora","Total Hora",
                                "Observaciones"};
        //Crear el modelo de la tabla - con encabezados
        DefaultTableModel model = new DefaultTableModel(nomColumnas, 0);
        //asignar a la tabla el modelo completo
        tabla.setModel(model);
        //Asignar el scrollpane con la vista de datos
        JscrollTabla.setViewportView(tabla);

        // Aplicar validación de solo texto en campos de nombres y apellidos
        agregarValidacionTexto(JtextFieldPNombre);
        agregarValidacionTexto(JtextFieldSNombre);
        agregarValidacionTexto(JtextFieldPApellido);
        agregarValidacionTexto(JtextFieldSApellido);

        // Aplicar validación de solo números en campos de identificación y valor por hora
        agregarValidacionNumero(JtextFieldNumeroI);
        agregarValidacionNumero(JtextFieldVHora);

        guardarButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(validarFormulario()){
                            String numID = JtextFieldNumeroI.getText().trim();

                            if (cedulaRepetida(numID)) {
                                JOptionPane.showMessageDialog(null, "El numero de identificación ya existe","Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }else{
                                Object[] nuevafila = capturarDatos();
                                model.addRow(nuevafila);
                                //limpiarDatos();
                            }
                            }else{
                            JOptionPane.showMessageDialog(null, "Por favor complete los datos", "error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
        );
        //activar el filtro de tabla
        filtro = new TableRowSorter<>(model);
        tabla.setRowSorter(filtro);

        //Botón de exportar en Excel
        JbuttonExportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExportarExcel obj;

                try {
                    obj = new ExportarExcel();
                    obj.exportarExcel(tabla);
                } catch (IOException ex) {
                    System.out.println("Error: " + ex);
                }

            }
        });

        JbuttonPdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarPDF();
            }
        });

        buscarTablaButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        aplicarFiltro();
                    }
                }
        );
        setVisible(true);
    }

    //metodo para validar que los campos esten completos
    private boolean validarFormulario(){
        if(JtextFieldPNombre.getText().trim().isEmpty() || JtextFieldSNombre.getText().trim().isEmpty() ||
        JtextFieldPApellido.getText().trim().isEmpty() || JtextFieldSApellido.getText().trim().isEmpty()||
        JtextFieldNumeroI.getText().trim().isEmpty() || JtextFieldVHora.getText().trim().isEmpty() || JtextAreaObserv.getText().trim().isEmpty()){
            return false;
        }
        if(!masculinoRadioButton.isSelected() && !femeninoRadioButton.isSelected() && !noBinarioRadioButton.isSelected() && lgbtiqRadioButton.isSelected()){
            return false;
        }
        if(!lunesCheckBox.isSelected() && !martesCheckBox.isSelected() && !miercolesCheckBox.isSelected() && !juevesCheckBox.isSelected() &&
        viernesCheckBox.isSelected()){
            return false;
        }
        if(JcomboBoxTipo.getSelectedItem()==null){
            return false;
        }
        return true;
    }

    //metodo para capturar datos
    private Object[] capturarDatos(){
        String primerNombre = JtextFieldPNombre.getText();
        String segundoNombre = JtextFieldSNombre.getText();
        String primerApellido = JtextFieldPApellido.getText();
        String segundoApellido = JtextFieldSApellido.getText();

        String genero ="";
        if(masculinoRadioButton.isSelected()){
            genero = "masculino";
        }else if(femeninoRadioButton.isSelected()){
            genero = "femenino";
        }else if(noBinarioRadioButton.isSelected()){
            genero = "No Binario";
        }else if(lgbtiqRadioButton.isSelected()){
            genero = "LGBTIQ+";
        }

        String tipoID = (String)JcomboBoxTipo.getSelectedItem();

        String numID = JtextFieldNumeroI.getText();

        StringBuilder dias = new StringBuilder();
        if(lunesCheckBox.isSelected())
            dias.append("lunes");
        if(martesCheckBox.isSelected())
            dias.append("martes");
        if(miercolesCheckBox.isSelected())
            dias.append("miercoles");
        if(juevesCheckBox.isSelected())
            dias.append("jueves");
        if(viernesCheckBox.isSelected())
            dias.append("viernes");

        String vhora = JtextFieldVHora.getText();
        String tothora = JtextFieldTHoras.getText();
        String observ = JtextAreaObserv.getText();

        return new Object[]{
            primerNombre, segundoNombre, primerApellido, segundoApellido, genero, tipoID, numID, dias.toString().trim(), vhora, tothora, observ
        };
    }
//Método para validar si la cédula está o no repetida en la tabla
    private boolean cedulaRepetida(String cedula){
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String  cedulaExiste = (String) model.getValueAt(i, 6);
            if(cedulaExiste.equals(cedula)){
                return true;
            }
        }
        return false;
    }
//Método para filtrar la tabla
    private void aplicarFiltro(){
        String texto = JTextFieldBusqueda.getText();
        try {
            filtro.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 6));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null , "Debe colocar al menos tres caracteres para la Busqueda");
            filtro.setRowFilter(null);
        }
    }

    // Método para permitir solo texto (letras y espacios) en el campo especificado
    private void agregarValidacionTexto(JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                    e.consume(); // Ignorar el carácter si no es letra o espacio
                }
            }
        });
    }

    // Método para permitir solo números en el campo especificado
    private void agregarValidacionNumero(JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignorar el carácter si no es un dígito
                }
            }
        });
    }

    //Metodo para exportar en pdf
    private void exportarPDF() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("DatosTabla.pdf"));
            document.open();

            // Agregar icono al documento (reemplaza "icono.png" por la ruta de tu imagen)
            Image icono = Image.getInstance("C:/Users/DELL/Documents/usuarios/JframesPrincipal/src/img/presentation.png");
            icono.scaleToFit(50, 50); // Ajusta el tamaño del icono si es necesario
            icono.setAlignment(Element.ALIGN_LEFT);
            document.add(icono);

            // Fecha y hora del documento
            String fechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Font fontFechaHora = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Paragraph fechaHoraParrafo = new Paragraph("Fecha y hora de exportación: " + fechaHora, fontFechaHora);
            fechaHoraParrafo.setAlignment(Element.ALIGN_RIGHT);
            document.add(fechaHoraParrafo);
            document.add(new Paragraph(" ")); // Espacio entre fecha y título

            // Título del documento
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Datos Personales", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" ")); // Espacio entre título y tabla

            // Crear tabla PDF con el mismo número de columnas que la tabla JTable
            PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());

            // Añadir encabezados de la tabla
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(tabla.getColumnName(i)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Añadir filas de la tabla
            for (int rows = 0; rows < tabla.getRowCount(); rows++) {
                for (int cols = 0; cols < tabla.getColumnCount(); cols++) {
                    pdfTable.addCell(tabla.getValueAt(rows, cols).toString());
                }
            }

            document.add(pdfTable);
            JOptionPane.showMessageDialog(null, "Exportación a PDF realizada con éxito.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al exportar a PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            document.close();
        }
    }


        //Metodo principal de ejecución
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new Principal());
    }

}
