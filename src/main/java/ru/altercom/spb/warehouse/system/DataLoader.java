package ru.altercom.spb.warehouse.system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.altercom.spb.warehouse.item.Item;
import ru.altercom.spb.warehouse.item.ItemRepository;
import ru.altercom.spb.warehouse.items_balance.ItemsBalanceService;
import ru.altercom.spb.warehouse.purchase.Purchase;
import ru.altercom.spb.warehouse.purchase.PurchaseRepository;
import ru.altercom.spb.warehouse.purchase.PurchaseRow;
import ru.altercom.spb.warehouse.purchase.PurchaseRowRepository;
import ru.altercom.spb.warehouse.receipt.Receipt;
import ru.altercom.spb.warehouse.receipt.ReceiptRepository;
import ru.altercom.spb.warehouse.receipt.ReceiptRow;
import ru.altercom.spb.warehouse.receipt.ReceiptRowRepository;
import ru.altercom.spb.warehouse.transfer.Transfer;
import ru.altercom.spb.warehouse.transfer.TransferRepository;
import ru.altercom.spb.warehouse.transfer.TransferRow;
import ru.altercom.spb.warehouse.transfer.TransferRowRepository;
import ru.altercom.spb.warehouse.warehouse.Warehouse;
import ru.altercom.spb.warehouse.warehouse.WarehouseRepository;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

//@Component
public class DataLoader implements CommandLineRunner {

    private final ItemRepository itemRepo;
    private final WarehouseRepository warehouseRepo;
    private final ReceiptRepository receiptRepo;
    private final ReceiptRowRepository receiptRowRepo;
    private final PurchaseRepository purchaseRepo;
    private final PurchaseRowRepository purchaseRowRepo;
    private final TransferRepository transferRepo;
    private final TransferRowRepository transferRowRepo;
    private final ItemsBalanceService itemsBalanceService;

    public DataLoader(ItemRepository itemRepo,
                      WarehouseRepository warehouseRepo,
                      ReceiptRepository receiptRepo,
                      ReceiptRowRepository receiptRowRepo,
                      PurchaseRepository purchaseRepo,
                      PurchaseRowRepository purchaseRowRepo,
                      TransferRepository transferRepo,
                      TransferRowRepository transferRowRepo,
                      ItemsBalanceService itemsBalanceService) {
        this.itemRepo = itemRepo;
        this.warehouseRepo = warehouseRepo;
        this.receiptRepo = receiptRepo;
        this.receiptRowRepo = receiptRowRepo;
        this.purchaseRepo = purchaseRepo;
        this.purchaseRowRepo = purchaseRowRepo;
        this.transferRepo = transferRepo;
        this.transferRowRepo = transferRowRepo;
        this.itemsBalanceService = itemsBalanceService;
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

            var receipt = new Receipt(null, date, warehouseId, comment);

            receiptMap.put(id, receiptRepo.save(receipt));
        }

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
            var receiptRow = new ReceiptRow(null, receiptId, itemId, quantity);

            receiptRowRepo.save(receiptRow);

            itemsBalanceService.save(receiptMap.get(receipt), List.of(receiptRow));
        }

        var purchaseMap = new HashMap<Long, Purchase>();
        nodeList = doc.getElementsByTagName("purchase");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();

            var id = Long.parseLong(attr.getNamedItem("id").getTextContent());
            var date = LocalDate.parse(attr.getNamedItem("date").getTextContent(), formatter);
            var warehouse = Long.parseLong(attr.getNamedItem("warehouse").getTextContent());
            var comment = attr.getNamedItem("comment").getTextContent();

            var warehouseId = warehouseMap.get(warehouse).getId();

            var purchase = new Purchase(null, date, warehouseId, comment);

            purchaseMap.put(id, purchaseRepo.save(purchase));
        }

        nodeList = doc.getElementsByTagName("purchase_row");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();

            var purchase = Long.parseLong(attr.getNamedItem("purchase").getTextContent());
            var purchase_key = Long.parseLong(attr.getNamedItem("purchase_key").getTextContent());
            var item = Long.parseLong(attr.getNamedItem("item").getTextContent());
            var quantity = new BigDecimal(attr.getNamedItem("quantity").getTextContent());

            var purchaseId = purchaseMap.get(purchase).getId();
            var itemId = itemMap.get(item).getId();
            var purchaseRow = new PurchaseRow(null, purchaseId, itemId, quantity);

            purchaseRowRepo.save(purchaseRow);

            itemsBalanceService.save(purchaseMap.get(purchase), List.of(purchaseRow));
        }

        var transferMap = new HashMap<Long, Transfer>();
        nodeList = doc.getElementsByTagName("transfer");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();

            var id = Long.parseLong(attr.getNamedItem("id").getTextContent());
            var date = LocalDate.parse(attr.getNamedItem("date").getTextContent(), formatter);
            var warehouseFrom = Long.parseLong(attr.getNamedItem("warehouseFrom").getTextContent());
            var warehouseTo = Long.parseLong(attr.getNamedItem("warehouseTo").getTextContent());
            var comment = attr.getNamedItem("comment").getTextContent();

            var warehouseFromId = warehouseMap.get(warehouseFrom).getId();
            var warehouseToId = warehouseMap.get(warehouseTo).getId();

            var transfer = new Transfer(null, date, warehouseFromId, warehouseToId, comment);

            transferMap.put(id, transferRepo.save(transfer));
        }

        nodeList = doc.getElementsByTagName("transfer_row");
        for (var i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var attr = node.getAttributes();

            var transfer = Long.parseLong(attr.getNamedItem("transfer").getTextContent());
            var transfer_key = Long.parseLong(attr.getNamedItem("transfer_key").getTextContent());
            var item = Long.parseLong(attr.getNamedItem("item").getTextContent());
            var quantity = new BigDecimal(attr.getNamedItem("quantity").getTextContent());

            var transferId = transferMap.get(transfer).getId();
            var itemId = itemMap.get(item).getId();
            var transferRow = new TransferRow(null, transferId, itemId, quantity);

            transferRowRepo.save(transferRow);

            itemsBalanceService.save(transferMap.get(transfer), List.of(transferRow));
        }

    }
}
