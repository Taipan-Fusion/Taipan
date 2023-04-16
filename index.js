const prompt = require('prompt-sync')();
const ship = {
  cannons: 25,
  health: 100,
  cargoUnits: 100,
  hold: 100,
  cargoSpaceOpium: 0,
  cargoSpaceSilk: 0,
  cargoSpaceArms: 0,
  cargoSpaceGeneral: 0
}

const player = {
  bank: 0,
  cash: 10000,
  debt: 0,
  location: "Hong Kong"
}

const prices = {
  opium: 0,
  silk: 0,
  arms: 0,
  general: 0
}

const warehouse = {
  cargoSpaceOpium: 0,
  cargoSpaceSilk: 0,
  cargoSpaceArms: 0,
  cargoSpaceGeneral: 0,
  inUse: 0,
  vacant: 10000
}

const gameAttributes = {
  month: 1,
  eventChanceSea: 0.5,
  eventChancePort: 0.25,
  liYuenFactor: 0.005
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
  while (input !== "e") {
    if (player.location === "Hong Kong") {
      if (ship.health < 100) {
        shipyard()
      }
      moneylender()
    }
    priceDisplay()
    generalPrompt()
    let rnd = Math.random()
    //console.log(rnd)
    if (rnd <= gameAttributes.eventChanceSea) {
      eventSea()
    }
    turnProgression()
  }
  console.log("Game Terminated.")
}

function time() {
  return (gameAttributes.month * 0.002) + 1
}

function priceGenerator(max) {
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
  prices.opium = priceGenerator(1000)
  prices.silk = priceGenerator(100)
  prices.arms = priceGenerator(10)
  prices.general = priceGenerator(1)
  console.log("Taipan, prices per unit here are: \n"
    + "Opium:", prices.opium.toString() + "\t" + "Silk:", prices.silk.toString() + "\n"
  + "Arms:", prices.arms.toString() + "\t" + "General:", prices.general.toString())
}

function generalPrompt() {
  while (true) {
    console.log("Player:", player, "Ship:", ship, "Warehouse:", warehouse, "Date:", gameAttributes)
    console.log("Taipan, prices per unit here are: \n"
      + "Opium:", prices.opium.toString() + "\t" + "Silk:", prices.silk.toString() + "\n"
    + "Arms:", prices.arms.toString() + "\t" + "General:", prices.general.toString())
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

function buy() {
  loop1: while (true) {
    let input = prompt("What do you wish to buy, Taipan? ")
    if (input === "o") {
      while (true) {
        let input = prompt("How many units of Opium do you want to buy? ")
        let inputAmount = parseInt(input)
        if (inputAmount * prices.opium > player.cash) {

        } else {
          ship.cargoSpaceOpium += inputAmount
          player.cash -= inputAmount * prices.opium
          ship.hold -= inputAmount
          break loop1
        }
      }
    } else if (input === "s") {
      while (true) {
        let input = prompt("How many units of Silk do you want to buy? ")
        let inputAmount = parseInt(input)
        if (inputAmount * prices.silk > player.cash) {

        } else {
          ship.cargoSpaceSilk += inputAmount
          player.cash -= inputAmount * prices.silk
          ship.hold -= inputAmount
          break loop1
        }
      }

    } else if (input === "a") {
      while (true) {
        let input = prompt("How many units of Arms do you want to buy? ")
        let inputAmount = parseInt(input)
        if (inputAmount * prices.arms > player.cash) {

        } else {
          ship.cargoSpaceArms += inputAmount
          player.cash -= inputAmount * prices.arms
          ship.hold -= inputAmount
          break loop1
        }
      }
    } else if (input === "g") {
      while (true) {
        let input = prompt("How many units of General do you want to buy? ")
        let inputAmount = parseInt(input)
        if (inputAmount * prices.general > player.cash) {

        } else {
          ship.cargoSpaceGeneral += inputAmount
          player.cash -= inputAmount * prices.general
          ship.hold -= inputAmount
          break loop1
        }
      }
    } else {

    }
  }
}

function sell() {
  loop1: while (true) {
    let input = prompt("What do you wish to sell, Taipan? ")
    if (input === "o") {
      while (true) {
        let input = prompt("How many units of Opium do you want to sell? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceOpium) {

        } else {
          ship.cargoSpaceOpium -= inputAmount
          player.cash += inputAmount * prices.opium
          ship.hold += inputAmount
          break loop1
        }
      }
    } else if (input === "s") {
      while (true) {
        let input = prompt("How many units of Silk do you want to sell? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceSilk) {

        } else {
          ship.cargoSpaceSilk -= inputAmount
          player.cash += inputAmount * prices.silk
          ship.hold += inputAmount
          break loop1
        }
      }

    } else if (input === "a") {
      while (true) {
        let input = prompt("How many units of Arms do you want to sell? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceArms) {

        } else {
          ship.cargoSpaceArms -= inputAmount
          player.cash += inputAmount * prices.arms
          ship.hold += inputAmount
          break loop1
        }
      }
    } else if (input === "g") {
      while (true) {
        let input = prompt("How many units of General do you want to sell? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceGeneral) {

        } else {
          ship.cargoSpaceGeneral -= inputAmount
          player.cash += inputAmount * prices.general
          ship.hold += inputAmount
          break loop1
        }
      }
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
      } else {
        player.cash -= inputAmount
        player.bank += inputAmount
        break
      }
    }
    while (true) {
      let input = prompt("How much will you withdraw? ")
      let inputAmount = parseInt(input)
      if (inputAmount > player.bank) {
        console.log("Taipan, you only have " + player.bank.toString(), "in your bank.")
      } else {
        player.cash += inputAmount
        player.bank -= inputAmount
        break
      }
    }
    break
  }
}

function transferCargo() {
  while (true) {
    if (ship.cargoSpaceOpium > 0) {
      while (true) {
        input = prompt("How much Opium shall I move to the warehouse, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceOpium) {
          console.log("You only have", ship.cargoSpaceOpium.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold += inputAmount
          warehouse.inUse += inputAmount
          warehouse.cargoSpaceOpium += inputAmount
          warehouse.vacant -= inputAmount
          ship.cargoSpaceOpium -= inputAmount
          break
        } else {

        }
      }
    }
    break
  }
  while (true) {
    if (ship.cargoSpaceSilk > 0) {
      while (true) {
        input = prompt("How much Silk shall I move to the warehouse, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceSilk) {
          console.log("You only have", ship.cargoSpaceSilk.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold += inputAmount
          warehouse.inUse += inputAmount
          warehouse.cargoSpaceSilk += inputAmount
          warehouse.vacant -= inputAmount
          ship.cargoSpaceSilk -= inputAmount
          break
        } else {

        }
      }
    }
    break
  }
  while (true) {
    if (ship.cargoSpaceArms > 0) {
      while (true) {
        input = prompt("How much Arms shall I move to the warehouse, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceArms) {
          console.log("You only have", ship.cargoSpaceArms.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold += inputAmount
          warehouse.inUse += inputAmount
          warehouse.cargoSpaceArms += inputAmount
          warehouse.vacant -= inputAmount
          ship.cargoSpaceArms -= inputAmount
          break
        } else {

        }
      }
    }
    break
  }
  while (true) {
    if (ship.cargoSpaceGeneral > 0) {
      while (true) {
        input = prompt("How much General shall I move to the warehouse, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > ship.cargoSpaceGeneral) {
          console.log("You only have", ship.cargoSpaceGeneral.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold += inputAmount
          warehouse.inUse += inputAmount
          warehouse.cargoSpaceGeneral += inputAmount
          warehouse.vacant -= inputAmount
          ship.cargoSpaceGeneral -= inputAmount
          break
        } else {

        }
      }
    }
    break
  }
  while (true) {
    if (warehouse.cargoSpaceOpium > 0) {
      while (true) {
        input = prompt("How much Opium shall I move aboard ship, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > warehouse.cargoSpaceOpium) {
          console.log("You only have", warehouse.cargoSpaceOpium.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold -= inputAmount
          warehouse.inUse -= inputAmount
          warehouse.cargoSpaceOpium -= inputAmount
          warehouse.vacant += inputAmount
          ship.cargoSpaceOpium += inputAmount
          break
        } else {

        }
      }
    }
    break
  }
  while (true) {
    if (warehouse.cargoSpaceSilk > 0) {
      while (true) {
        input = prompt("How much Silk shall I move aboard ship, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > warehouse.cargoSpaceSilk) {
          console.log("You only have", warehouse.cargoSpaceSilk.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold -= inputAmount
          warehouse.inUse -= inputAmount
          warehouse.cargoSpaceSilk -= inputAmount
          warehouse.vacant += inputAmount
          ship.cargoSpaceSilk += inputAmount
          break
        } else {

        }
      }
    }
    break
  }
  while (true) {
    if (warehouse.cargoSpaceArms > 0) {
      while (true) {
        input = prompt("How much Arms shall I move aboard ship, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > warehouse.cargoSpaceArms) {
          console.log("You only have", warehouse.cargoSpaceArms.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold -= inputAmount
          warehouse.inUse -= inputAmount
          warehouse.cargoSpaceArms -= inputAmount
          warehouse.vacant += inputAmount
          ship.cargoSpaceArms += inputAmount
          break
        } else {

        }
      }
    }
    break
  }
  while (true) {
    if (warehouse.cargoSpaceGeneral > 0) {
      while (true) {
        input = prompt("How much General shall I move aboard ship, Taipan? ")
        let inputAmount = parseInt(input)
        if (inputAmount > warehouse.cargoSpaceGeneral) {
          console.log("You only have", warehouse.cargoSpaceGeneral.toString(), "Taipan.")
        } else if (Number.isInteger(inputAmount)) {
          ship.hold -= inputAmount
          warehouse.inUse -= inputAmount
          warehouse.cargoSpaceGeneral -= inputAmount
          warehouse.vacant += inputAmount
          ship.cargoSpaceGeneral += inputAmount
          break
        } else {

        }
      }
    }
    break
  }
}


// function handleLocationInput(input) {
//   if (input === "1") {
//     if (player.location === "Hong Kong") {
//       console.log("You're already here, Taipan.")
//     } else {
//       console.log("Location: Hong Kong")
//       player.location = "Hong Kong"
//       break
//     }
//   }
// }

function quitTrading() {
  while (true) {

    let input = prompt("Taipan, do you wish to go to: " +
      "1) Hong Kong, 2) Shanghai, 3) Nagasaki, 4) Saigon, 5) Manila, 6) Singapore, 7) Batavia ? ")

    if (input === "1") {
      if (player.location === "Hong Kong") {
        console.log("You're already here, Taipan.")
      } else {
        console.log("Location: Hong Kong")
        player.location = "Hong Kong"
        break
      }
    }


    if (input === "2") {
      if (player.location === "Shanghai") {
        console.log("You're already here, Taipan.")
      } else {

        console.log("Location: Shanghai")
        player.location = "Shanghai"
        break
      }
    }


    if (input === "3") {
      if (player.location === "Nagasaki") {
        console.log("You're already here, Taipan.")
      } else {

        console.log("Location: Nagasaki")
        player.location = "Nagasaki"
        break
      }
    }


    if (input === "4") {
      if (player.location === "Saigon") {
        console.log("You're already here, Taipan.")
      } else {

        console.log("Location: Saigon")
        player.location = "Saigon"
        break
      }
    }


    if (input === "5") {
      if (player.location === "Manila") {
        console.log("You're already here, Taipan.")
      } else {

        console.log("Location: Manila")
        player.location = "Manila"
        break
      }
    }


    if (input === "6") {
      if (player.location === "Singapore") {
        console.log("You're already here, Taipan.")
      } else {

        console.log("Location: Singapore")
        player.location = "Singapore"
        break
      }
    }


    if (input === "7") {
      if (player.location === "Batavia") {
        console.log("You're already here, Taipan.")
      } else {

        console.log("Location: Batavia")
        player.location = "Batavia"
        break
      }
    }


  }
}

function retire() {
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

function eventSea() {
  let randomNumber = (Math.random() + 0.5) * 0.5
  gameAttributes.eventChanceSea = randomNumber
  let rndPirates = Math.random()
  let rndStorm = Math.random()
  let rndLiYuen = Math.random()
  let number;
  if (rndLiYuen <= gameAttributes.liYuenFactor) {
    number = pirateGenerator(1 + Math.floor(gameAttributes.month / 4) + Math.floor(ship.cargoUnits / 50), 48 + Math.floor(gameAttributes.month / 4 + Math.floor(ship.cargoUnits / 50)))
    console.log(number.toString(), "ships from Li Yuen's private fleet, Taipan!")
    pirates("Li Yuen", number)
  } else {
    if (rndPirates <= 0.3) {
      number = pirateGenerator(Math.floor(gameAttributes.month / 6) + Math.floor(ship.cargoUnits / 100) + Math.round(ship.cargoSpaceOpium / 100 + ship.cargoSpaceSilk / 100), 38 + Math.floor(gameAttributes.month / 6 + Math.floor(ship.cargoUnits / 100)))
      console.log(number.toString(), "hostile ships, Taipan!")
      pirates("Regular", number)
    }
  }
  if (rndStorm <= 0.3) {
    storm()
  }
}

function eventPort() {

}

function shipyard() {
  let rndShipFix = Math.round(pirateGenerator(1, 200) * (1 + gameAttributes.month / 12) * (1 + ship.cargoUnits / 100))
  console.log("Captain McHenry has arrived from the Hong Kong shipyard. He says: ''Tis a pity that your ship is at", ship.health.toString(), + ".",
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
    combat(2, 0.2, number, 2)
  } else {
    combat(1, 0.1, number, 1)
  }
}

function combat(damageCoefficient, gunKnockoutChance, number, pirateResistanceCoefficient) {
  let resistanceRatio = gameAttributes.month / ship.cargoUnits * 2
  let runRatio = 0.25 * 200 / (ship.cargoUnits + 5 * number)
  let damageToShip = Math.round(resistanceRatio * damageCoefficient * (Math.random() + 1) * 25 * damageCoefficient)
  let rndGunKnockout = Math.random()
  console.log(number, "ships attacking, Taipan!")
  loop1: while (true) {
    let input = prompt("Shall we fight or run, Taipan? ")
    if (input === "f") {
      let numberSank = 0
      for (let i = 0; i < ship.cannons; i++) {
        let chanceOfPirateShipSinking = Math.random()
        if (chanceOfPirateShipSinking <= 0.45 * 250 / (gameAttributes.month * pirateResistanceCoefficient + 250 * pirateResistanceCoefficient)) {
          number--
          numberSank++
        }
        if (number === 0) {
          console.log("Sank", numberSank, "buggers, Taipan!")
          console.log("We got them all, Taipan!")
          let booty = Math.round(numberSank * pirateGenerator(1, 100) * pirateGenerator(1, 10) * pirateGenerator(1, 10) * (1 + gameAttributes.month / 12))
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