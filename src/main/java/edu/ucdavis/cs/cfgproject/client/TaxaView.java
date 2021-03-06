package edu.ucdavis.cs.cfgproject.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonProperties;

public class TaxaView extends SimpleContainer {

	private EventBus eventBus;
	private TaxonProperties taxonProperites = GWT.create(TaxonProperties.class);
	private ListStore<Taxon> taxaStore;
	private Grid<Taxon> taxaGrid;
	private ColumnConfig<Taxon, String> nameColumn;
	private static TaxonomyImages taxonomyImages = GWT.create(TaxonomyImages.class);
	
	public TaxaView(EventBus eventBus) {
		this.eventBus = eventBus;

		add(createTaxaGrid());
	}
	
	private Grid<Taxon> createTaxaGrid() {
		taxaStore = new ListStore<Taxon>(taxonProperites.key());
		taxaStore.addSortInfo(new StoreSortInfo<Taxon>(new IdentityValueProvider<Taxon>(), SortDir.ASC));
		nameColumn = new ColumnConfig<Taxon, String>(taxonProperites.name(), 
				50, SafeHtmlUtils.fromTrustedString("<b>Remaining Taxa</b>"));
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		columns.add(nameColumn);
	    ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		taxaGrid = new Grid<Taxon>(taxaStore, cm);
		taxaGrid.getView().setAutoExpandColumn(nameColumn);
		taxaGrid.getView().setStripeRows(true);
		taxaGrid.getView().setColumnLines(true);
		taxaGrid.getView().setSortingEnabled(true);
		nameColumn.setCell(new AbstractCell<String>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	String value, SafeHtmlBuilder sb) {
				ImageResource image = taxonomyImages.yellow(); 
				String text = value;
				try {
					String[] parts = value.split(":");
					String[] ranks = parts[0].split(";");
					String[] rankNameValue = ranks[ranks.length - 1].split("=");
					String rankName = rankNameValue[0];
					
					String[] dates = parts[1].split(",");
					String author = dates[0].split("=")[1];
					String date = dates[1].split("=")[1];
					text = rankNameValue[1] + " " + author + ", " + date;
					
					switch(rankName) {
						case "FAMILY":
							image = taxonomyImages.f();
							break;
						case "GENUS":
							image = taxonomyImages.g();
							break;
						case "SPECIES":
							image = taxonomyImages.s();
							break;
						default:
							image = taxonomyImages.yellow();
					}
				} catch(Throwable t) {	}
				
				ImageResourceRenderer renderer = new ImageResourceRenderer();
				renderer.render(image, sb);
				sb.append(SafeHtmlUtils.fromTrustedString("<span style=\"padding-left: 10px;\">" + text + "</span>"));
			}
		});
	    return taxaGrid;
	}

	public void setTaxa(final TaxonMatrix taxonMatrix) {
		nameColumn.setHeader("Taxa  (Remaining;" + taxonMatrix.size() + ")");
		taxaGrid.getView().getHeader().refresh();
		taxaStore.clear();
		taxaStore.addAll(taxonMatrix.getTaxa());
	}	

}
