package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("itemMemoryStorage")
@Data
@AllArgsConstructor
public class ItemMemoryStorage {

    private Map<Long, Item> items = new HashMap<>();

    public Item addNewItem(Item item) {
        item.setId(getNewId());
        items.put(item.getId(), item);
        return item;
    }

    //Нужна проверка принадлежности айтема юзеру
    public Item updateItem(Item item) {
        return items.put(item.getId(), item); //пока возвращается новое значение
    }

    public Item deleteItem(Long id) {
        return items.remove(id);
    }

    public Item getItemById(Long id) {
        return items.get(id);
    }

    public List<Item> getAllOwnersItems(Long id) {
        return items.keySet().stream()
                .map(items::get)
                .filter(item -> item.getOwner().equals(id))
                .toList();
    }

    public List<Item> getItemsByContext(String query) {
        return items.keySet().stream()
                .map(items::get)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public boolean isItemExists(Long id) {
        return items.containsKey(id);
    }

    public Long getNewId() {
        long id = items.keySet().stream()
                .mapToLong(l -> l)
                .max().orElse(0);
        return ++id;
    }

}
