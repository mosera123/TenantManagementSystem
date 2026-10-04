

import java.util.Locale
import kotlin.system.exitProcess

const val CURRENCY = "KES"
const val LINE_WIDTH = 48

class Tenant(
    val name: String,
    val apartmentNumber: Int,
    rentAmount: Double
) {
    var isPaid: Boolean = false

    var rentAmount: Double = 0.0
        get() {
            if (announceAccess) println("Rent amount accessed.")
            return field
        }
        set(value) {
            if (value >= 0) {
                field = value
            } else {
                println("Rent amount cannot be negative.")
            }
        }

    init {
        this.rentAmount = rentAmount
    }

    fun payRent() {
        isPaid = true
        println("Rent paid successfully by $name")
    }

    companion object {
        var announceAccess: Boolean = true
    }
}

class Apartment(
    val apartmentNumber: Int,
    val tenants: MutableList<Tenant> = mutableListOf()
) {
    fun addTenant(tenant: Tenant) {
        if (tenant.apartmentNumber != apartmentNumber) {
            println("Cannot add ${tenant.name}: tenant belongs to Apartment ${tenant.apartmentNumber}, not $apartmentNumber.")
            return
        }
        tenants.add(tenant)
        println("${tenant.name} has been added to Apartment $apartmentNumber.")
    }

    fun showTenants() {
        println("Apartment: $apartmentNumber")
        if (tenants.isEmpty()) {
            println("No tenants in this apartment yet.")
            return
        }
        for (tenant in tenants) {
            println("Tenant: ${tenant.name}")
            println("Rent: ${tenant.rentAmount}")
            println("Rent paid: ${tenant.isPaid}")
        }
    }
}

fun kes(amount: Double): String = "$CURRENCY " + String.format(Locale.US, "%,.2f", amount)

fun line(char: Char = '-') = println(char.toString().repeat(LINE_WIDTH))

fun title(text: String) {
    line('=')
    println(" $text")
    line('=')
}

fun ask(prompt: String): String {
    print("$prompt\n> ")
    return readlnOrNull()?.trim() ?: run {
        println("\nInput closed. Goodbye!")
        exitProcess(0)
    }
}

fun askText(prompt: String): String {
    while (true) {
        val answer = ask(prompt)
        if (answer.isNotEmpty()) return answer
        println("Input cannot be empty. Please try again.")
    }
}

fun askInt(prompt: String, min: Int = 1): Int {
    while (true) {
        val number = ask(prompt).toIntOrNull()
        if (number != null && number >= min) return number
        println("Please enter a whole number (minimum $min).")
    }
}

fun askDouble(prompt: String): Double {
    while (true) {
        val number = ask(prompt).toDoubleOrNull()
        if (number != null) return number
        println("Please enter a valid number, e.g. 15000")
    }
}

fun chooseTenant(apartment: Apartment): Tenant? {
    if (apartment.tenants.isEmpty()) {
        println("Apartment ${apartment.apartmentNumber} has no tenants yet. Add one first (option 1).")
        return null
    }
    println("Tenants in Apartment ${apartment.apartmentNumber}:")
    apartment.tenants.forEachIndexed { index, tenant ->
        val status = if (tenant.isPaid) "PAID" else "NOT PAID"
        println("  ${index + 1}. ${tenant.name} ($status)")
    }
    val choice = askInt("Enter the tenant number:")
    return apartment.tenants.getOrNull(choice - 1) ?: run {
        println("There is no tenant with that number.")
        null
    }
}

fun findApartment(apartments: MutableList<Apartment>, number: Int): Apartment? =
    apartments.find { it.apartmentNumber == number }

fun addTenantFlow(apartments: MutableList<Apartment>) {
    title("ADD A TENANT")
    val number = askInt("Enter the apartment number (e.g. 101):")
    val name = askText("Enter the tenant's name:")
    val rent = askDouble("Enter the monthly rent in $CURRENCY:")

    println("Creating tenant record...")
    val tenant = Tenant(name, number, rent)

    if (rent < 0) {
        println("Tenant was NOT added because the rent was invalid. Please try again.")
        return
    }

    val apartment = findApartment(apartments, number) ?: Apartment(number).also {
        apartments.add(it)
        println("New apartment $number created.")
    }
    apartment.addTenant(tenant)
}

fun payRentFlow(apartments: MutableList<Apartment>) {
    title("RECORD RENT PAYMENT")
    val apartment = findApartment(apartments, askInt("Enter the apartment number:"))
    if (apartment == null) {
        println("Apartment not found.")
        return
    }
    val tenant = chooseTenant(apartment) ?: return
    println("Updating payment status...")
    if (tenant.isPaid) {
        println("${tenant.name} has already paid this month's rent.")
    } else {
        tenant.payRent()
    }
}

fun changeRentFlow(apartments: MutableList<Apartment>) {
    title("CHANGE A TENANT'S RENT")
    val apartment = findApartment(apartments, askInt("Enter the apartment number:"))
    if (apartment == null) {
        println("Apartment not found.")
        return
    }
    val tenant = chooseTenant(apartment) ?: return
    val oldRent = tenant.rentAmount
    val newRent = askDouble("Enter the new monthly rent in $CURRENCY (try a negative number to see validation):")
    tenant.rentAmount = newRent
    val currentRent = tenant.rentAmount
    if (currentRent == oldRent && newRent != oldRent) {
        println("Result: change REJECTED. Rent stays at ${kes(currentRent)}.")
    } else {
        println("Result: rent for ${tenant.name} is now ${kes(currentRent)}.")
    }
}

fun viewApartmentFlow(apartments: MutableList<Apartment>) {
    title("VIEW APARTMENT TENANTS")
    val apartment = findApartment(apartments, askInt("Enter the apartment number:"))
    if (apartment == null) {
        println("Apartment not found.")
        return
    }
    line()
    apartment.showTenants()
    line()
}

fun summaryFlow(apartments: MutableList<Apartment>) {
    title("RENT SUMMARY - ALL APARTMENTS")
    if (apartments.isEmpty()) {
        println("No apartments registered yet. Use option 1 to add a tenant.")
        return
    }
    println("Calculating totals...")
    var totalExpected = 0.0
    var totalCollected = 0.0
    for (apartment in apartments) {
        apartment.showTenants()
        totalExpected += apartment.tenants.sumOf { it.rentAmount }
        totalCollected += apartment.tenants.filter { it.isPaid }.sumOf { it.rentAmount }
        line()
    }
    println("Total rent expected : ${kes(totalExpected)}")
    println("Total rent collected: ${kes(totalCollected)}")
    println("Outstanding balance : ${kes(totalExpected - totalCollected)}")
    line()
}

fun runLabDemo() {
    Tenant.announceAccess = true

    title("LAB 3 DEMO: TASKS 1 & 2 - OBJECTS + CONSTRUCTOR")
    println("Creating two tenants using the primary constructor...")
    val tenant1 = Tenant("John", 101, 15000.0)
    val tenant2 = Tenant("Mary", 101, 18000.0)
    println("Only John pays his rent:")
    tenant1.payRent()
    println("John paid? ${tenant1.isPaid}")
    println("Mary paid? ${tenant2.isPaid}")

    title("LAB 3 DEMO: TASK 3 - SETTER VALIDATION")
    val tenant3 = Tenant("Peter", 102, 20000.0)
    println("Trying to change Peter's rent to -500...")
    tenant3.rentAmount = -500.0
    val rentAfter = tenant3.rentAmount
    println("Rent after attempted change: $rentAfter")
    println(if (rentAfter == -500.0) "Negative value was ACCEPTED." else "Negative value was REJECTED.")

    title("LAB 3 DEMO: TASK 4 - GETTER")
    println("Running: println(tenant.rentAmount)")
    println(tenant3.rentAmount)

    Tenant.announceAccess = false
    println("(Getter message switched off for the next section.)")

    title("LAB 3 DEMO: TASKS 5, 6 & 7 - APARTMENT")
    val apartment = Apartment(101)
    apartment.addTenant(tenant1)
    apartment.addTenant(tenant2)
    println("Verification: Apartment 101 now stores ${apartment.tenants.size} tenants.")
    line()
    apartment.showTenants()
    line()
}

fun main() {
    val apartments = mutableListOf<Apartment>()

    title("TENANT MANAGEMENT SYSTEM")
    println("Welcome! This program manages tenants and rent for apartments.")
    println("PART 1: an automatic walkthrough of Lab 3 (Tasks 1-7) with sample data.")
    println("PART 2: an interactive menu where YOU enter tenants and payments.")
    println()

    runLabDemo()
    ask("Part 1 complete. Press Enter to continue to the interactive menu.")

    Tenant.announceAccess = false
    println("(The 'Rent amount accessed.' message is now switched off to keep the menu readable.)")
    println("Type the number of an option and press Enter.")

    var running = true
    while (running) {
        println()
        println("MAIN MENU")
        println("  1. Add a tenant to an apartment")
        println("  2. Record a rent payment")
        println("  3. Change a tenant's rent")
        println("  4. View tenants in an apartment")
        println("  5. View rent summary (all apartments)")
        println("  6. Re-run the Lab 3 demo (Tasks 1-7)")
        println("  0. Exit")

        when (ask("Choose an option:")) {
            "1" -> addTenantFlow(apartments)
            "2" -> payRentFlow(apartments)
            "3" -> changeRentFlow(apartments)
            "4" -> viewApartmentFlow(apartments)
            "5" -> summaryFlow(apartments)
            "6" -> runLabDemo()
            "0" -> running = false
            else -> println("Invalid option. Please enter a number from the menu.")
        }
    }

    println("Thank you for using the Tenant Management System. Goodbye!")
}