package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.ArrayList;
import java.util.List;

public class InvoiceRequestMockBuilder {

	private ClientData client;
	private List<Product> productsList;

	private InvoiceRequestMockBuilder() {
		productsList = new ArrayList<>();
	}

	public static InvoiceRequestMockBuilder builder() {
		return new InvoiceRequestMockBuilder();
	}

	public InvoiceRequestMockBuilder withClient(ClientData client) {
		this.client = client;
		return this;
	}

	public InvoiceRequestMockBuilder withProductsCount(int count) {
		for (int i = 0; i < count; i++) {
			productsList.add(new Product(
					Mockito.mock(Id.class),
					Mockito.mock(Money.class),
					"",
					ProductType.STANDARD
			));
		}
		return this;
	}

	public InvoiceRequest build() {

		InvoiceRequest request;
		if (client == null) {
			client = Mockito.mock(ClientData.class);
		}
		request = new InvoiceRequest(client);

		for (Product prod : this.productsList) {
			RequestItem item = new RequestItem(prod.generateSnapshot(), 1, Money.ZERO);
			request.add(item);
		}

		return request;
	}

}
