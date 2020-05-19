package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.ArrayList;
import java.util.List;

public class InvoiceRequestBuilder {

	private ClientData client;
	private List<Product> productsList;

	private InvoiceRequestBuilder() {
		productsList = new ArrayList<>();
	}

	public static InvoiceRequestBuilder builder() {
		return new InvoiceRequestBuilder();
	}

	public InvoiceRequestBuilder withClient(ClientData client) {
		this.client = client;
		return this;
	}

	public InvoiceRequestBuilder withProductsCount(int count) {
		for (int i = 0; i < count; i++) {
			productsList.add(new Product(
					Id.generate(),
					Money.ZERO,
					"",
					ProductType.STANDARD
			));
		}
		return this;
	}

	public InvoiceRequest build() {

		InvoiceRequest request;
		if (client == null) {
			client = new ClientData(Id.generate(), "");
		}
		request = new InvoiceRequest(client);

		for (Product prod : this.productsList) {
			RequestItem item = new RequestItem(prod.generateSnapshot(), 1, Money.ZERO);
			request.add(item);
		}

		return request;
	}

}
