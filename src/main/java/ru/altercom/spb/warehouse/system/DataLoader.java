package ru.altercom.spb.warehouse.system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.altercom.spb.warehouse.item.Item;
import ru.altercom.spb.warehouse.item.ItemRepository;
import ru.altercom.spb.warehouse.receipt.Receipt;
import ru.altercom.spb.warehouse.receipt.ReceiptRepository;
import ru.altercom.spb.warehouse.receipt.ReceiptRow;
import ru.altercom.spb.warehouse.warehouse.Warehouse;
import ru.altercom.spb.warehouse.warehouse.WarehouseRepository;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//@Component
public class DataLoader implements CommandLineRunner {

    private final ItemRepository itemRepo;
    private final WarehouseRepository warehouseRepo;
    private final ReceiptRepository receiptRepo;

    public DataLoader(ItemRepository itemRepo, WarehouseRepository warehouseRepo, ReceiptRepository receiptRepo) {
        this.itemRepo = itemRepo;
        this.warehouseRepo = warehouseRepo;
        this.receiptRepo = receiptRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        parseXml();
    }

    private void parseXml() throws Exception {
        var resource = new ClassPathResource("data/data.xml");

        var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var is = new BufferedInputStream(resource.getInputStream());
        var doc = builder.parse(is);

        doc.getDocumentElement().normalize();

        var itemMap = new HashMap<Long, Item>();
        var nodeList = doc.getElementsByTagName("item");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();
            var id = Long.parseLong(attr.getNamedItem("id").getTextContent());
            var name = attr.getNamedItem("name").getTextContent();

            itemMap.put(id, itemRepo.save(new Item(null, name)));
        }

        var warehouseMap = new HashMap<Long, Warehouse>();
        nodeList = doc.getElementsByTagName("warehouse");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();

            var id = Long.parseLong(attr.getNamedItem("id").getTextContent());
            var name = attr.getNamedItem("name").getTextContent();

            warehouseMap.put(id, warehouseRepo.save(new Warehouse(null, name)));
        }

        var receiptMap = new HashMap<Long, Receipt>();
        nodeList = doc.getElementsByTagName("receipt");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();

            var id = Long.parseLong(attr.getNamedItem("id").getTextContent());
            var date = LocalDate.parse(attr.getNamedItem("date").getTextContent(), formatter);
            var warehouse = Long.parseLong(attr.getNamedItem("warehouse").getTextContent());
            var comment = attr.getNamedItem("comment").getTextContent();

            var warehouseId = warehouseMap.get(warehouse).getId();

            var receipt = new Receipt(null, date, warehouseId, comment, new ArrayList<>());

            receiptMap.put(id, receiptRepo.save(receipt));
        }

        var receiptRowMap = new HashMap<Long, List<ReceiptRow>>();
        nodeList = doc.getElementsByTagName("receipt_row");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();

            var receipt = Long.parseLong(attr.getNamedItem("receipt").getTextContent());
            var receipt_key = Long.parseLong(attr.getNamedItem("receipt_key").getTextContent());
            var item = Long.parseLong(attr.getNamedItem("item").getTextContent());
            var quantity = new BigDecimal(attr.getNamedItem("quantity").getTextContent());

            var receiptId = receiptMap.get(receipt).getId();
            var itemId = itemMap.get(item).getId();
            var receiptRow = new ReceiptRow(itemId, quantity);

            receiptRowMap.putIfAbsent(receiptId, new ArrayList<ReceiptRow>());
            receiptRowMap.get(receiptId).add(receiptRow);

        }

        var receiptList = new ArrayList<Receipt>();
        for (var entrySet: receiptRowMap.entrySet()) {
            var receipt = receiptMap.get(entrySet.getKey());
            receiptList.add(receipt.withRows(entrySet.getValue()));
        }
        receiptRepo.saveAll(receiptList);

    }
}
