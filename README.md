## MixAnyEnchants

**Minecraft Version: Spigot 1.18**

### About
MixAnyEnchant allows you to combine not allowed enchantments in an anvil. 
The behavior as well as the experience cost is based on the default minecraft implementation. 
The logic only intervenes, if an enchantment conflict exists. Otherwise, the default logic applies.

### Experience Cost
The experience cost is calculated with the following formula 
`(cost = (conflicting enchantments) + (penalty of target item) + (penalty of sacrificed item) + (rename cost))`

- `(conflicting enchantments)` calculates the total cost of the enchantments that will be added. This cost is different for items and books (books are cheaper).
- `(penalty)` is the amount of times the item has been used in an anvil and is calculated with the following formula `(penalty) = 2^(Anvil use count) - 1`.
- `(rename cost)` adds 1 to the total cost, if the item is renamed

Calculation is based on https://minecraft.fandom.com/wiki/Anvil_mechanics

### Permissions
- _mixanyenchant.use_ - to use this feature


### Dependencies
- Truth 1.1.3
    - https://github.com/google/truth
- mockk 1.12.1
    - https://github.com/mockk/mockk