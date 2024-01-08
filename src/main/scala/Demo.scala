// https://youtrack.jetbrains.com/issue/KT-44845
//import com.treemap.DefaultTreeMap

import com.treemap.{TreeMapModel, TreeMapSettings, _}
import org.mkui.colormap.MutableColorMap
import org.mkui.font.crossplatform.CPFont
import org.mkui.swing.{HierarchicalComboBox, Orientation, SingleSelectionComboBoxModel}
import org.molap.dataframe.{DataFrame, JsonDataFrame}
import org.mkui.labeling.EnhancedLabel
import org.mkui.palette.{FixedPalette, PaletteFactory}

import java.awt._
import java.io.InputStream
import java.util
import javax.swing._


object Demo extends App {
//  TreeMap.setLicenseKey("My Company", "ABC12-ABC12-ABC12-ABC12-ABC12-ABC12")

  val inputStream: InputStream = getClass.getResourceAsStream("Forbes Global 2000 - 2021.json")
  val dataFrame: DataFrame[Integer, String, AnyRef] = JsonDataFrame.fromInputStream(inputStream)
  val treeMap: AbstractTreeMap[Integer, String] = new DefaultTreeMap[Integer, String](dataFrame)

  val model: TreeMapModel[AbstractTreeMapNode[Integer, String], Integer, String] = treeMap.getModel
  val settings: TreeMapSettings[String] = model.getSettings

  // General
  settings.setRendering(RenderingFactory.getFLAT)

  // Group by
  settings.setGroupByColumns(util.Arrays.asList("Sector", "Industry"))

  // Size
  settings.setSizeColumn("Market Value")

  // Color
  settings.setColorColumn("Profits")
  val profitsSettings: TreeMapColumnSettings = settings.getColumnSettings("Profits")
  val negpos: FixedPalette = new PaletteFactory().get("negpos").getPalette
  val colorMap: MutableColorMap = model.getColorMap("Profits")
  colorMap.setPalette(negpos)
  colorMap.getInterval.setValue(-(88.205), 176.41)

  // Label
  val companySettings: TreeMapColumnSettings = settings.getColumnSettings("Company")
  companySettings.setLabelingFont(new CPFont(new Font("Helvetica", Font.PLAIN, 9)).getNativeFont()) // 9 points is the minimum size that will be displayed

  companySettings.setLabelingMinimumCharactersToDisplay(5)
  companySettings.setLabelingResizeTextToFitShape(true)
  companySettings.setLabelingVerticalAlignment(EnhancedLabel.CENTER)
  companySettings.setLabelingHorizontalAlignment(EnhancedLabel.CENTER)

  val configuration: JPanel = createConfiguration(model, settings)

  val splitPane: JSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, configuration, treeMap.getComponent.getNativeComponent)

  val mainPanel: JPanel = new JPanel(new BorderLayout)
  mainPanel.add(createGroupBy(Orientation.Horizontal, model, settings), BorderLayout.NORTH)
  mainPanel.add(splitPane)

  val frame: JFrame = new JFrame("TreeMap")

  frame.getContentPane.add(mainPanel)
  frame.setSize(1024, 768)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setLocationRelativeTo(null)
  frame.setVisible(true)

  private def createConfiguration(model: TreeMapModel[AbstractTreeMapNode[Integer, String], Integer, String], settings: TreeMapSettings[String]): JPanel = {
    val configuration: JPanel = new JPanel
    configuration.setLayout(new BoxLayout(configuration, BoxLayout.PAGE_AXIS))
    configuration.add(createGroupBy(Orientation.Vertical, model, settings))
    configuration.add(createSizeComboBox(model, settings))
    configuration.add(createColorComboBox(model, settings))
    configuration.add(createAlgorithmComboBox(settings.getDefaultColumnSettings))
    configuration.add(createRenderingComboBox(settings))
    configuration.add(Box.createGlue)
    return configuration
  }

  private def createGroupBy(orientation: Orientation, model: TreeMapModel[AbstractTreeMapNode[Integer, String], Integer, String], settings: TreeMapSettings[String]): HierarchicalComboBox[String] = {
    val groupBy: HierarchicalComboBox[String] = new HierarchicalComboBox[String](orientation, settings.getGroupByColumnsSelection, model.getGroupByColumns) {
      override def getMaximumSize: Dimension = {
        return super.getPreferredSize
      }
    }
    groupBy.setBorder(BorderFactory.createTitledBorder("Group by"))
    groupBy.setAlignmentX(0)
    return groupBy
  }

  private def createSizeComboBox(model: TreeMapModel[AbstractTreeMapNode[Integer, String], Integer, String], settings: TreeMapSettings[String]): JComboBox[String] = {
    val sizeComboBox: JComboBox[String] = new JComboBox[String]((new SingleSelectionComboBoxModel[String](settings.getSizeColumnSelection, model.getSizeColumns))) {
      override def getMaximumSize: Dimension = {
        return super.getPreferredSize
      }
    }
    sizeComboBox.setBorder(BorderFactory.createTitledBorder("Size"))
    sizeComboBox.setAlignmentX(0)
    return sizeComboBox
  }

  private def createColorComboBox(model: TreeMapModel[AbstractTreeMapNode[Integer, String], Integer, String], settings: TreeMapSettings[String]): JComboBox[String] = {
    val sizeComboBox: JComboBox[String] = new JComboBox[String]((new SingleSelectionComboBoxModel[String](settings.getColorColumnSelection, model.getColorColumns))) {
      override def getMaximumSize: Dimension = {
        return super.getPreferredSize
      }
    }
    sizeComboBox.setBorder(BorderFactory.createTitledBorder("Size"))
    sizeComboBox.setAlignmentX(0)
    return sizeComboBox
  }

  private def createAlgorithmComboBox(settings: TreeMapColumnSettings): JComboBox[Algorithm] = {
    val algorithmComboBox: JComboBox[Algorithm] = new JComboBox[Algorithm]((new SingleSelectionComboBoxModel[Algorithm](settings.getAlgorithmProperty, AlgorithmFactory.getInstance.getAlgorithms))) {
      override def getMaximumSize: Dimension = {
        return super.getPreferredSize
      }
    }
    algorithmComboBox.setBorder(BorderFactory.createTitledBorder("Algorithm"))
    algorithmComboBox.setAlignmentX(0)
    return algorithmComboBox
  }

  private def createRenderingComboBox(settings: TreeMapSettings[String]): JComboBox[Rendering] = {
    val renderingComboBox: JComboBox[Rendering] = new JComboBox[Rendering]((new SingleSelectionComboBoxModel[Rendering](settings.getRenderingSelection, RenderingFactory.getInstance.getRenderings))) {
      override def getMaximumSize: Dimension = {
        return super.getPreferredSize
      }
    }
    renderingComboBox.setBorder(BorderFactory.createTitledBorder("Rendering"))
    renderingComboBox.setAlignmentX(0)
    return renderingComboBox
  }
}