const prompt = require('prompt-sync')();
const ship = {
  cannons: 5,
  health: 100,
  cargoUnits: 150,
  hold: 100,
  Opium: 0,
  Silk: 0,
  Arms: 0,
  General: 0
}

const player = {
  bank: 0,
  cash: 1000,
  debt: 0,
  location: "Hong Kong"
}

const prices = {
  Opium: 0,
  Silk: 0,
  Arms: 0,
  General: 0
}

const warehouse = {
  Opium: 0,
  Silk: 0,
  Arms: 0,
  General: 0,
  inUse: 0,
  vacant: 10000
}

const gameAttributes = {
  month: 1,
  eventChanceSea: 0.5,
  eventChancePort: 0.25,
  liYuenFactor: 0.8,
  liYuenExtortionFactor: 0.8,
  status: "Running"
}

const locationsMap = {
  "1": "Hong Kong",
  "2": "Shanghai",
  "3": "Nagasaki",
  "4": "Saigon",
  "5": "Manila",
  "6": "Singapore",
  "7": "Batavia"
};


function game() {
  console.log("Welcome to Taipan!")
  let input
  while (gameAttributes.status !== "Terminated") {
    if (player.location === "Hong Kong") {
      if (ship.health < 100) {
        shipyard()
      }
      if (Math.random() <= gameAttributes.liYuenExtortionFactor) {
        LiYuen()
      }
      gameAttributes.liYuenExtortionFactor += 0.0175
      moneylender()
    }
    if (Math.random() <= gameAttributes.eventChancePort) {
      eventPort()
    }
    if (Math.random() <= 1) {
      randomPrice()
    }
    priceDisplay()
    generalPrompt()
    if (gameAttributes.status === "Terminated") {
      break
    }
    //put this before the second if to replace the calls to Random()
    // |
    //\/
    let rnd = Math.random()
    //console.log(rnd)
    let status = true
    if (rnd <= gameAttributes.liYuenFactor) {
      number = pirateGenerator(1 + Math.floor(gameAttributes.month / 4) + Math.floor(ship.cargoUnits / 50), 10 + 2 * (Math.floor(gameAttributes.month / 4 + Math.floor(ship.cargoUnits / 50))))
      console.log(number.toString(), "ships from Li Yuen's private fleet, Taipan!")
      pirates("Li Yuen", number)
      status = false
    }
    if (rnd <= gameAttributes.eventChanceSea) {
      eventSea(status)
    }
    turnProgression()
  }
  console.log("Game Terminated.")
}

function time() {
  return (gameAttributes.month * 0.0015) + 1
}

function priceGenerator(max) {
  /* 
    Feature Suggestion: Prices should be based on what each port has in stock
    Implementation: There should be a class called Port, which stores the info for a generic port. Then, a const list of ports can be created for each port to store the info. The port class contains the following:
      -Name:String
  -Supply:Object[string : int]
    -MaxSupply: int
    -FirstRestock:string
    -PriceScalar: float(?)
The price of each commodity would be decided with something like this : price = MaxSupply/Supply[type]*PriceScalar
Additionally, an extra cost can be applied(can be negaitve lol) if you buy all the stuff from a port
A port will restock a random amount when taipan leaves the port. It will always try to restock its firstRestock first, then will try to restock a random good. If either fail, it will restock a random good until something suceeds or all goods fail.(the latter can be done with a conditional at the top of the restock action)
 */
  function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * time() * (max - min + 1)) + min;
  }
  return getRandomInt(5, 25) * max
}

function pirateGenerator(min, max) {
  function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * time() * (max - min + 1)) + min;
  }
  return getRandomInt(min, max)
}

function priceDisplay() {
  prices.Opium = priceGenerator(1000)
  prices.Silk = priceGenerator(100)
  prices.Arms = priceGenerator(10)
  prices.General = priceGenerator(1)
  console.log("Taipan, prices per unit here are: \n"
    + "Opium:", prices.Opium.toString() + "\t" + "Silk:", prices.Silk.toString() + "\n"
  + "Arms:", prices.Arms.toString() + "\t" + "General:", prices.General.toString())
}

function generalPrompt() {
  while (true) {
    console.log("Player:", player, "Ship:", ship, "Warehouse:", warehouse, "Date:", gameAttributes)
    console.log("Taipan, prices per unit here are: \n"
      + "Opium:", prices.Opium.toString() + "\t" + "Silk:", prices.Silk.toString() + "\n"
    + "Arms:", prices.Arms.toString() + "\t" + "General:", prices.General.toString())
    if (player.location === "Hong Kong") {
      if (player.bank + player.cash >= 1000000) {
        let input = prompt("Shall I Buy, Sell, Visit Bank, Transfer Cargo, Quit trading, or Retire? ")
        if (input === "b") {
          buy()
        } else if (input === "s") {
          sell()
        } else if (input === "v") {
          visitBank()
        } else if (input === "t") {
          transferCargo()
        } else if (input === "q") {
          if (ship.hold < 0) {
            console.log("Your ship will be overburdened, Taipan!")
          } else {
            quitTrading()
            break
          }
        } else if (input === "r") {
          retire()
          break
        } else {

        }
      } else {
        let input = prompt("Shall I Buy, Sell, Visit Bank, Transfer Cargo, or Quit trading? ")
        if (input === "b") {
          buy()
        } else if (input === "s") {
          sell()
        } else if (input === "v") {
          visitBank()
        } else if (input === "t") {
          transferCargo()
        } else if (input === "q") {
          if (ship.hold < 0) {
            console.log("Your ship will be overburdened, Taipan!")
          } else {
            quitTrading()
            break
          }
        } else {

        }
      }
    } else {
      let input = prompt("Shall I Buy, Sell, or Quit trading? ")
      if (input === "b") {
        buy()
      } else if (input === "s") {
        sell()
      } else if (input === "q") {
        if (ship.hold < 0) {
          console.log("Your ship will be overburdened, Taipan!")
        } else {
          quitTrading()
          break
        }
      } else {

      }
    }
  }
}

function buyHandler(product) {
  while (true) {
    let affordableNumber = Math.floor(player.cash / prices[product])
    let input = prompt(`How many units of ${product} do you want to buy? You can afford ${affordableNumber} ${product}. `)
    let inputAmount = parseInt(input)
    if (inputAmount * prices[product] > player.cash) {

    } else {
      ship[product] += inputAmount
      player.cash -= inputAmount * prices[product]
      ship.hold -= inputAmount
      return false
    }
  }
}

function buy() {
  let status = true
  while (status) {
    let input = prompt("What do you wish to buy, Taipan? ")
    if (input === "o") {
      status = buyHandler("Opium")
    } else if (input === "s") {
      status = buyHandler("Silk")
    } else if (input === "a") {
      status = buyHandler("Arms")
    } else if (input === "g") {
      status = buyHandler("General")
    } else {
    }
  }
}

function sellHandler(product) {
  while (true) {
    let input = prompt(`How many units of ${product} do you want to sell? `)
    let inputAmount = parseInt(input)
    if (inputAmount > ship[product]) {

    } else {
      ship[product] -= inputAmount
      player.cash += inputAmount * prices[product]
      ship.hold += inputAmount
      return false
    }
  }
}

function sell() {
  let status = true
  while (status) {
    let input = prompt("What do you wish to sell, Taipan? ")
    if (input === "o") {
      status = sellHandler("Opium")
    } else if (input === "s") {
      status = sellHandler("Silk")
    } else if (input === "a") {
      status = sellHandler("Arms")
    } else if (input === "g") {
      status = sellHandler("General")
    } else {

    }
  }
}

function visitBank() {
  loop1: while (true) {
    while (true) {
      let input = prompt("How much will you deposit? ")
      let inputAmount = parseInt(input)
      if (inputAmount > player.cash) {
        console.log("Taipan, you only have " + player.cash.toString(), "in your wallet.")
      } else if (Number.isInteger(inputAmount)) {
        player.cash -= inputAmount
        player.bank += inputAmount
        break
      } else {

      }
    }
    while (true) {
      let input = prompt("How much will you withdraw? ")
      let inputAmount = parseInt(input)
      if (inputAmount > player.bank) {
        console.log("Taipan, you only have " + player.bank.toString(), "in your bank.")
      } else if (Number.isInteger(inputAmount)) {
        player.cash += inputAmount
        player.bank -= inputAmount
        break
      } else {

      }
    }
    break
  }
}


function transferCargoHandlerToWarehouse(product) {
  while (true) {
    if (ship[product] > 0) {
      while (true) {
        input = prompt(`How much ${product} shall I move to the warehouse, Taipan? `)
        let inputAmount = parseInt(input)
        if (inputAmount > ship[product]) {
          console.log("You only have", ship[product].toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold += inputAmount
          warehouse.inUse += inputAmount
          warehouse[product] += inputAmount
          warehouse.vacant -= inputAmount
          ship[product] -= inputAmount
          break
        } else {

        }
      }
    }
    break
  }
}

function transferCargoHandlerToShip(product) {
  while (true) {
    if (warehouse[product] > 0) {
      while (true) {
        input = prompt(`How much ${product} shall I move aboard ship, Taipan? `)
        let inputAmount = parseInt(input)
        if (inputAmount > warehouse[product]) {
          console.log("You only have", warehouse[product].toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold -= inputAmount
          warehouse.inUse -= inputAmount
          warehouse[product] -= inputAmount
          warehouse.vacant += inputAmount
          ship[product] += inputAmount
          break
        } else {

        }
      }
    }
    break
  }
}

function transferCargo() {
  transferCargoHandlerToWarehouse("Opium")
  transferCargoHandlerToWarehouse("Silk")
  transferCargoHandlerToWarehouse("Arms")
  transferCargoHandlerToWarehouse("General")
  transferCargoHandlerToShip("Opium")
  transferCargoHandlerToShip("Silk")
  transferCargoHandlerToShip("Arms")
  transferCargoHandlerToShip("General")
}


function handleLocationInput(input) {
  if (player.location === locationsMap[input]) {
    console.log("You're already here, Taipan.")
  } else if (Number.isInteger(parseInt(input))) {
    player.location = locationsMap[input]
    return "Done"
  } else {

  }
}

function quitTrading() {
  let status = "Incomplete"
  while (status !== "Done") {

    let input = prompt("Taipan, do you wish to go to: " +
      "1) Hong Kong, 2) Shanghai, 3) Nagasaki, 4) Saigon, 5) Manila, 6) Singapore, 7) Batavia ? ")
    status = handleLocationInput(input)
  }
}

function retire() {
  gameAttributes.status = "Terminated"
  console.log(player, ship, warehouse, gameAttributes)
}

function turnProgression() {
  console.log("Arriving at", player.location)
  gameAttributes.month += 1
  player.debt *= 1.2
  player.bank *= 1.05
  player.debt = Math.round(player.debt)
  player.bank = Math.round(player.bank)
}

function eventSea(status) {
  let randomNumber = (Math.random() + 0.5) * 0.5
  gameAttributes.eventChanceSea = randomNumber
  let rndPirates = Math.random()
  let rndStorm = Math.random()
  let rndLiYuen = Math.random()
  let number;
  if (rndLiYuen <= gameAttributes.liYuenFactor && status) {
    number = pirateGenerator(1 + Math.floor(gameAttributes.month / 4) + Math.floor(ship.cargoUnits / 50), 10 + 2 * (Math.floor(gameAttributes.month / 4 + Math.floor(ship.cargoUnits / 50))))
    console.log(number.toString(), "ships from Li Yuen's private fleet, Taipan!")
    pirates("Li Yuen", number)
  } else {
    if (rndPirates <= 0.3 && status) {
      number = pirateGenerator(Math.floor(gameAttributes.month / 6) + Math.floor(ship.cargoUnits / 100) + Math.round(ship.Opium / 100 + ship.Silk / 100), 5 + 2 * (Math.floor(gameAttributes.month / 6 + Math.floor(ship.cargoUnits / 100))))
      console.log(number.toString(), "hostile ships, Taipan!")
      pirates("Regular", number)
    }
  }
  if (rndStorm <= 0.3) {
    let result = storm()
    if (result === "done") {
      throw new Error("Done.")
    }
  }
}

function LiYuen() {
  while (true) {
    let amount = Math.round((Math.random() + 0.1) * player.cash * 1.1 * Math.random())
    let input = prompt("Li Yuen asks " + amount + " in donation to the temple of Tin Hau, the Sea Goddess. Will you pay? ")
    if (input === "y") {
      if (amount > player.cash) {
        player.cash = 0
        player.debt = amount - player.cash
        gameAttributes.liYuenExtortionFactor = 0.1
        gameAttributes.liYuenFactor = 0.05
        break
      } else {
        player.cash -= amount
        gameAttributes.liYuenExtortionFactor = 0.05
        gameAttributes.liYuenFactor = 0.025
        break
      }
    } else {
      gameAttributes.liYuenExtortionFactor = 0.8
      gameAttributes.liYuenFactor = 0.8
      break
    }
  }
}

function eventPort() {
  let randomNumber = (Math.random() + 0.5) * 0.5
  gameAttributes.eventChancePort = randomNumber
  let newShipChance = Math.random()
  let moreGunsChance = Math.random()
  let getRobbed = Math.random()
  let opiumConfiscation = Math.random()
  if (newShipChance <= 0.55) {
    newShip()
  }
  if (moreGunsChance <= 0.65) {
    moreGuns()
  }
  if (getRobbed <= 0.15) {
    let amount = Math.round((Math.random() + 0.1) * player.cash)
    console.log("You were beaten and robbed", amount, "in cash, Taipan!")
    player.cash -= amount
  }
  if (opiumConfiscation <= opiumConfiscationChance()) {
    let amount = Math.round((Math.random()) * player.cash)
    console.log("Bad Joss! Officials confiscated your opium and fined you", amount, "in cash, Taipan!")
    player.cash -= amount
    ship.hold += ship.Opium
    ship.Opium = 0
  }
}

function newShip() {
  while (true) {
    let amount = Math.round((Math.random() + 0.1) * player.cash * 0.5)
    if (ship.health < 100) {
      let input = prompt("Would you like to change your damaged ship with one fifty units larger for " + amount + " cash? ")
      if (input === "y") {
        ship.health = 100
        ship.cargoUnits += 50
        ship.hold += 50
        player.cash -= amount
        break
      } else {
        break
      }
    } else {
      let input = prompt("Would you like to change your ship with one fifty units larger for " + amount + " cash? ")
      if (input === "y") {
        ship.health = 100
        ship.cargoUnits += 50
        ship.hold += 50
        player.cash -= amount
        break
      } else {
        break
      }
    }
  }
}

function moreGuns() {
  while (true) {
    let amount = Math.round(((Math.random()) + 0.1) * player.cash * 0.5 * 0.3)
    let input = prompt("Would you like a random amount of guns for " + amount + " cash? ")
    if (ship.hold - 10 < 0) {
      console.log("Your ship would be overburdened, Taipan!")
      break
    } else {
      if (input === "y") {
        let numberOfGuns = pirateGenerator(1, 3)
        if (ship.hold - 10 < 10) {
          ship.hold -= 10
          ship.cannons += 1
          player.cash -= amount
          break
        } else if (ship.hold - 20 < 10) {
          if (numberOfGuns === 3) {
            let numberOfGuns2 = pirateGenerator(1, 2)
            ship.hold -= 10 * numberOfGuns2
            ship.cannons += numberOfGuns2
            player.cash -= amount * numberOfGuns2
            break
          } else {
            ship.hold -= numberOfGuns * 10
            ship.cannons += numberOfGuns
            player -= amount * numberOfGuns
            break
          }
        } else {
          ship.hold -= numberOfGuns * 10
          ship.cannons += numberOfGuns
          player.cash -= amount * numberOfGuns
          break
        }
      } else {
        break
      }
    }
  }
}

function opiumConfiscationChance() {
  return (ship.Opium / ship.cargoUnits) * 0.7
}

function randomPrice() {
  let arr = ["Opium", "Silk", "Arms", "General"]
  let arr2 = [1000, 100, 10, 1]
  let randomIndex = pirateGenerator(1, 4)
  let product = arr[randomIndex]
  let multiplier = arr2[randomIndex]
  console.log("Taipan!!!")
  console.log("Prices for " + product + " are wild!!!")
  let newPrice;
  if (Math.random() < 0.5) {
    newPrice = pirateGenerator(1, 4) * multiplier
  } else {
    newPrice = pirateGenerator(50, 1000) * multiplier
  }
  prices[product] = newPrice
  console.log(product + " is at " + newPrice + "!!!")
}

function shipyard() {
  let rndShipFix = Math.round(pirateGenerator(1, 200) * (1 + gameAttributes.month / 12) * (1 + ship.cargoUnits / 100))
  console.log("Captain McHenry has arrived from the Hong Kong Shipyards! He says: ''Tis a pity that your ship is at", ship.health.toString() + ".",
    "I'll fix", (100 - ship.health).toString() + "% health for", rndShipFix.toString() + ".")
  while (true) {
    let input = prompt("How much will you pay? ")
    let inputAmount = parseInt(input)
    if (inputAmount > player.cash) {
      console.log("Taipan, you only have", player.cash.toString(), "cash.")
    } else if (Number.isInteger(inputAmount)) {
      ship.health += Math.round((100 - ship.health) * inputAmount / rndShipFix)
      break
    } else {

    }
  }
}

function pirates(type, number) {
  if (type === "Li Yuen") {
    gameAttributes.liYuenFactor = 0.8
    gameAttributes.liYuenExtortionFactor = 0.8
    let result = combat(2, 0.2, number, 2)
    if (result === "Done") {
      throw new Error("Done.")
    }
  } else {
    let result = combat(1, 0.1, number, 1)
    if (result === "Done") {
      throw new Error("Done.")
    }
  }
}

function combat(damageCoefficient, gunKnockoutChance, number, pirateResistanceCoefficient) {
  let resistanceRatio = gameAttributes.month / ship.cargoUnits
  let runRatio = 0.25 * 200 / (ship.cargoUnits + 5 * number)
  let rndGunKnockout = Math.random()
  const number2 = number
  console.log(number, "ships attacking, Taipan!")
  loop1: while (true) {
    let damageToShip = Math.round(resistanceRatio * damageCoefficient ** 2 * (Math.random() + 1) * 25 * number / number2)
    let input = prompt("Shall we fight or run, Taipan? ")
    if (input === "f") {
      let numberSank = 0
      for (let i = 0; i < ship.cannons; i++) {
        let chanceOfPirateShipSinking = Math.random()
        if (chanceOfPirateShipSinking <= 0.5 * 250 / (gameAttributes.month * pirateResistanceCoefficient + 200 * pirateResistanceCoefficient)) {
          number--
          numberSank++
        }
        if (number === 0) {
          console.log("Sank", numberSank, "buggers, Taipan!")
          console.log("We got them all, Taipan!")
          let booty = Math.round(numberSank * pirateGenerator(5, 100) * pirateGenerator(2, 10) * pirateGenerator(1, 5) * (1 + gameAttributes.month / 12))
          player.cash += booty
          console.log("We got", booty, "in booty, Taipan!")
          break loop1
        }
      }
      console.log("Sank", numberSank, "buggers, Taipan!", number, "remain, Taipan!")
    } else {
      let numberRan = 0
      if (Math.random() < runRatio) {
        numberRan = pirateGenerator(number / 5, number)
        if (numberRan === number) {
          console.log("We got away from them, Taipan!")
          break loop1
        } else {
          number -= numberRan
          console.log("Can't escape them, Taipan, but we got away from", numberRan, "of them!")
        }
      } else {
        console.log("Can't escape them, Taipan!")
      }
    }
    console.log("They're firing on us, Taipan!")
    if (rndGunKnockout < gunKnockoutChance) {
      console.log("They hit a gun, Taipan!")
      ship.cannons--
      console.log(ship)
      rndGunKnockout = Math.random()
    } else {
      console.log(damageToShip)
      ship.health -= damageToShip
      console.log(ship)
    }
    if (ship.health === 0) {
      console.log("We're going down, Taipan!")
      retire()
    }
  }
}

function storm() {
  let chanceOfSinking = (100 - ship.health) / 200
  console.log("Storm, Taipan!")
  if (Math.random() < chanceOfSinking) {
    console.log("We're going down, Taipan!")
    retire()
  } else {
    console.log("We survived, Taipan!")
  }
}

function moneylender() {
  let input = prompt("Do you have business with Elder Brother Wu, the moneylender? ")
  while (input !== "e") {
    if (input === "y") {
      if (player.debt > 0) {
        let input = prompt("How much do you wish to repay him? ")
        let inputAmount = parseInt(input)
        if (inputAmount > player.cash) {
          console.log("You can't do that!")
        } else {
          if (inputAmount > player.debt) {
            player.cash -= inputAmount
            player.debt = 0
          } else {
            player.debt -= inputAmount
            player.cash -= inputAmount
          }
        }
      }
      let input = prompt("How much do you wish to borrow? ")
      let inputAmount = parseInt(input)
      if (inputAmount >= player.cash) {
        console.log("He won't loan you so much, Taipan!")
      } else {
        player.cash += inputAmount
        player.debt += inputAmount
        break
      }
    }
    else {
      break
    }
  }
}

game()