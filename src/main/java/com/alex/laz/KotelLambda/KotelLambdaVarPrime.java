package com.alex.laz.KotelLambda;

/**
 * Created by alex on 18-Dec-16.
 */
/**
 * Created by alex on 18-Dec-16.
 */

import com.opencsv.CSVReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static org.jfree.data.time.Second.parseSecond;
public class KotelLambdaVarPrime extends JFrame {
//    class KotelLambdaVarPrime1 extends LogAxis {}

    private static final int MAX = 8;
    //    24 години по 6 раз у кожній годині є 5 хвилина, за добу 120
    private static final int COUNT = 120;
    private static final int UNITS = 5;
    //    private static final Random random = new Random();
    private static String sDate = LocalDate.now().toString().replace("-", ""); //сьогоднішній день
    static String PATH = ("\\\\F7\\Logs\\" + sDate + ".log");
//    public NumberAxis domain;

    CSVReader reader;
    String[] readNextLine;


    /**
     * Construct a new frame
     *
     * @param title the frame title
     */
    public KotelLambdaVarPrime(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        final NumberAxis domain = new NumberAxis();

//        final DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries(createSeries(2));
        dataset.addSeries(createSeries(3));
        dataset.addSeries(createSeries(4));
//        dataset.addSeries("Series1", createSeries(1));
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(640, 480));
        this.add(chartPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Додати Дані");
        buttonPanel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = dataset.getSeriesCount()+2;
                if (n < 9) dataset.addSeries(createSeries(n));
            }
        });
        JButton remButton = new JButton("Прибрати Дані");
        buttonPanel.add(remButton);
        remButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = dataset.getSeriesCount() - 1;
                if (n > 0) dataset.removeSeries(n);
            }
        });
//************************************************

        JButton rangeButton = new JButton("Інтервал");
        buttonPanel.add(rangeButton);
        rangeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                public static Range expand(Range range,
//                double lowerMargin,
//                double upperMargin)
                int n = dataset.getSeriesCount() - 1;
                if (n > 0) dataset.removeSeries(n);
            }
        });
//************************************************
        final JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(UNITS, 1, COUNT, 1));
        spinner.addChangeListener(e -> {
            JSpinner s = (JSpinner) e.getSource();
            Number n = (Number) s.getValue();
            domain.setTickUnit(new NumberTickUnit(n.intValue()));
        });
        buttonPanel.add(spinner);
//************************************************
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Create a series
     *
     * @ return the series
     */
    private TimeSeries createSeries(int mean) {
        String sName;
        switch (mean) {
            case 0: sName = "Дата"; break;
            case 1: sName = "Час"; break;
            case 2: sName = "ДімПодача"; break;
            case 3: sName = "Триходовий"; break;
            case 4: sName = "Приміщення"; break;
            case 5: sName = "КотелПодача"; break;
            case 6: sName = "КотелЗворотня"; break;
            case 7: sName = "ДімЗворотня"; break;
            case 8: sName = "Вулиця";      break;
            default: sName = " ";          break;
        }
        final TimeSeries seriesX = new TimeSeries(sName);
        try {
            reader = new CSVReader(new FileReader(PATH));
//          Set up series
            // timeDoublicate для уникнення дублів,регулярка вибирає кожні 5 хвилин, а є ще секунди
            String timeDoublicate = "";
            String sTime = "";
            while ((readNextLine = reader.readNext()) != null) {
                sTime = readNextLine[1];
                // add values to series НИЖЧЕ МОЖЕ БУТИ ЧАС, АБО РЕГУЛЯРКА
                // "\\d\\d:\\d5:\\d\\d" - кожні 5 хвилин
                if (Pattern.matches("\\d\\d:\\d5:\\d\\d", sTime) && !sTime.substring(0,6).equals(timeDoublicate)){
                    timeDoublicate = sTime.substring(0,6);
                    try {
// тут конструюю дату з моєї і додаю час, для нормального відображення
                        String sDate = readNextLine[0];
                        sDate = sDate.trim();
//                        String[] sDateSplit = sDate.split("\\.");
//                        String sNewFormatData = sDateSplit[2] + "-" + sDateSplit[1] + "-" + sDateSplit[0];
                        String dayStr = sDate.substring(0, Math.min(10, sDate.length()));
                        String sNewFormatData = dayStr.substring(6,10) + "-" + dayStr.substring(3,5) + "-" + dayStr.substring(0,2);
                        Second Time = parseSecond(sNewFormatData+ " " + sTime);
// тут конструюю дату з моєї і додаю час, для нормального відображення
// mean вказує на номер стовбчика у файлі, з якого беру дані
                        double X = Double.valueOf(readNextLine[mean]);
                        seriesX.add(Time, X);
                    } catch (NumberFormatException ex) {System.out.println("Exception "+ex);}
                }
            }
        }
        catch (FileNotFoundException e) {System.out.println("File not found!");}
        catch (IOException e) {e.printStackTrace();}
        return seriesX;
    }
    /**
     * Create a chart.
     *
     * @param dataset the dataset
     * @return the chart
     */
    private JFreeChart createChart(XYDataset dataset) {

        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Котел", // chart title
                "Час", // domain axis label
                "Teмпература", // range axis label
                dataset,  // initial series
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
        );

        // set chart background
        chart.setBackgroundPaint(Color.white);

        // set a few custom plot features
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set the plot's axes to display integers
        TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setTickUnit(new NumberTickUnit(UNITS));
//************************************************
        System.out.println(domain.getRange());

//************************************************
        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setTickUnit(new NumberTickUnit(UNITS));
//        range.setStandardTickUnits(ticks);

        // render shapes and lines
        XYLineAndShapeRenderer renderer =
                new XYLineAndShapeRenderer(true, true);
        plot.setRenderer(renderer);
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);

        // set the renderer's stroke
        Stroke stroke = new BasicStroke(
                3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        renderer.setBaseOutlineStroke(stroke);

        plot.setDomainAxis(new DateAxis());
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

        // label the points
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(0);
/*        XYItemLabelGenerator generator =
                new StandardXYItemLabelGenerator(
                        StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
                        format, format);*/
        XYItemLabelGenerator generator = new  MyItemLabelGenerator();
        renderer.setBaseItemLabelGenerator(generator);
        renderer.setBaseItemLabelsVisible(true);

        return chart;
    }

    /** Main method */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                KotelLambdaVarPrime demo = new KotelLambdaVarPrime("Koтел");
                demo.pack();
                demo.setLocationRelativeTo(null);
                demo.setVisible(true);
            }
        });
    }
    class MyItemLabelGenerator extends StandardXYItemLabelGenerator {

        public String generateLabel (XYDataset dataset, int series, int item) {
            if (item%UNITS == 0 ) return String.valueOf(dataset.getY(series,item).intValue());
            else return "";

        }
    }
}
