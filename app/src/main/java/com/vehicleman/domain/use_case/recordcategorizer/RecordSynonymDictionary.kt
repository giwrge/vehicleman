package com.vehicleman.domain.use_case.recordcategorizer

import com.vehicleman.domain.model.category.RecordCategory

/**
 * Μεγάλο λεξικό συνωνύμων (English + Greek + Greeklish + slang)
 * που χαρτογραφεί keywords σε RecordCategory.
 *
 * Όλα τα keys ΠΡΕΠΕΙ να είναι ήδη normalized (lowercase, χωρίς τόνους).
 */
object RecordSynonymDictionary {

    /**
     * Κεντρικός χάρτης:
     * - key: normalized keyword ή φράση (π.χ. "fuel", "air filter", "lastixa")
     * - value: RecordCategory
     */
    val keywordToCategory: Map<String, RecordCategory> = buildMap {

        // --------------------------------------------------
        // ⛽ Fuel / Καύσιμα
        // --------------------------------------------------
        val fuel = RecordCategory.ExpenseCategory.Fuel.FuelPurchase
        put("fuel", fuel)
        put("gasoline", fuel)
        put("petrol", fuel)
        put("unleaded", fuel)
        put("unleaded95", fuel)
        put("unleaded98", fuel)
        put("unleaded100", fuel)
        put("gas", fuel)
        put("refuel", fuel)
        put("refueling", fuel)
        put("fill up", fuel)
        put("full tank", fuel)
        put("fulltank", fuel)

        // ελληνικά / greeklish
        put("βενζινη", fuel)
        put("φουλαρισμα", fuel)
        put("γεμισμα ρεζερβουαρ", fuel)
        put("γεμισμα ρεζερβουαρ", fuel)
        put("καυσιμα", fuel)
        put("καυσιμο", fuel)
        put("κασιμα", fuel)
        put("κασιμο", fuel)
        put("κασιμα", fuel)
        put("κασιμο", fuel)

        put("benzini", fuel)
        put("venzin", fuel)
        put("venzinis", fuel)
        put("kafsimo", fuel)
        put("kaysimo", fuel)
        put("kaysima", fuel)
        put("kafsima", fuel)
        put("foularisma", fuel)
        put("gemisma rezervuar", fuel)
        put("gemisma tank", fuel)

        // Diesel
        put("diesel", fuel)
        put("dizel", fuel)
        put("πετρελαιο", fuel)
        put("petrelaio", fuel)

        // LPG
        val lpg = RecordCategory.ExpenseCategory.Fuel.FuelPurchase
        put("lpg", lpg)
        put("autogas", lpg)
        put("υγραεριο", lpg)
        put("ygraerio", lpg)
        put("υγραεριου", lpg)

        // CNG κλπ – τα περνάμε επίσης ως fuel purchase
        put("cng", fuel)
        put("φυσικο αεριο", fuel)
        put("fisiko aerio", fuel)

        // --------------------------------------------------
        // 🔌 Electric charge
        // --------------------------------------------------
        val electric = RecordCategory.ExpenseCategory.Fuel.ElectricCharge
        put("charge", electric)
        put("charging", electric)
        put("ev charge", electric)
        put("fast charge", electric)
        put("quick charge", electric)
        put("dc fast", electric)
        put("ac charge", electric)

        put("φορτιση", electric)
        put("φορτιση αυτοκινητου", electric)
        put("φορτιση ev", electric)
        put("fartisi", electric)
        put("fartish", electric)
        put("electric charge", electric)
        put("ev", electric)

        // --------------------------------------------------
        // 🧴 AdBlue
        // --------------------------------------------------
        val adblue = RecordCategory.ExpenseCategory.Fuel.AdBlue
        put("adblue", adblue)
        put("ad blue", adblue)
        put("αdblου", adblue)
        put("υγρο adblue", adblue)

        // --------------------------------------------------
        // 💧 Fuel additives
        // --------------------------------------------------
        val fuelAdditives = RecordCategory.ExpenseCategory.Fuel.FuelAdditives
        put("additive", fuelAdditives)
        put("fuel additive", fuelAdditives)
        put("octane booster", fuelAdditives)
        put("καθαριστικο βενζινης", fuelAdditives)
        put("καθαριστικο πετρελαιου", fuelAdditives)
        put("προσθετο καυσιμου", fuelAdditives)
        put("pros8eto kafsimou", fuelAdditives)
        put("pros8eta", fuelAdditives)
        put("prosthiki pros8etou", fuelAdditives)

        // --------------------------------------------------
        // 🔧 Oil / Service
        // --------------------------------------------------
        val oilChange = RecordCategory.ExpenseCategory.Service.OilChange
        put("oil", oilChange)
        put("engine oil", oilChange)
        put("oil change", oilChange)
        put("change oil", oilChange)
        put("oil service", oilChange)

        put("αλλαγη λαδιων", oilChange)
        put("λαδια", oilChange)
        put("λαδι", oilChange)
        put("allagi ladion", oilChange)
        put("ladia", oilChange)
        put("ladi", oilChange)

        // Oil filter
        val oilFilter = RecordCategory.ExpenseCategory.Service.OilFilter
        put("oil filter", oilFilter)
        put("φιλτρο λαδιου", oilFilter)
        put("filtra ladion", oilFilter)
        put("filtr ladion", oilFilter)
        put("filtrou ladiou", oilFilter)

        // Air filter
        val airFilter = RecordCategory.ExpenseCategory.Service.AirFilter
        put("air filter", airFilter)
        put("airfilter", airFilter)
        put("φιλτρο αερα", airFilter)
        put("filtra aera", airFilter)
        put("aerofiltro", airFilter)

        // Cabin filter
        val cabinFilter = RecordCategory.ExpenseCategory.Service.CabinFilter
        put("cabin filter", cabinFilter)
        put("pollen filter", cabinFilter)
        put("φιλτρο καμπινας", cabinFilter)
        put("filtra kabinas", cabinFilter)
        put("microfilter", cabinFilter)

        // Fuel filter
        val fuelFilter = RecordCategory.ExpenseCategory.Service.FuelFilter
        put("fuel filter", fuelFilter)
        put("φιλτρο καυσιμου", fuelFilter)
        put("filtra kafsimou", fuelFilter)

        // Spark plugs
        val sparkPlugs = RecordCategory.ExpenseCategory.Service.SparkPlugs
        put("spark plug", sparkPlugs)
        put("spark plugs", sparkPlugs)
        put("μπουζι", sparkPlugs)
        put("mpouzi", sparkPlugs)

        // Antirust
        val antirust = RecordCategory.ExpenseCategory.Service.Antirust
        put("antirust", antirust)
        put("αντισκωριακο", antirust)
        put("antiskioriako", antirust)
        put("prostateutiko piso", antirust)

        // Timing belt / chain
        val timingBelt = RecordCategory.ExpenseCategory.Service.TimingBelt
        put("timing belt", timingBelt)
        put("ιμαντας χρονισμου", timingBelt)
        put("imantas xronismou", timingBelt)

        val timingChain = RecordCategory.ExpenseCategory.Service.TimingChain
        put("timing chain", timingChain)
        put("καδενα χρονισμου", timingChain)
        put("kadena xronismou", timingChain)

        // Antifreeze
        val antifreeze = RecordCategory.ExpenseCategory.Service.Antifreeze
        put("antifreeze", antifreeze)
        put("coolant", antifreeze)
        put("παραφλου", antifreeze)
        put("ψυκτικο υγρο", antifreeze)
        put("paraflu", antifreeze)

        // Brake fluid / Steering fluid
        val brakeFluid = RecordCategory.ExpenseCategory.Service.BrakeFluid
        put("brake fluid", brakeFluid)
        put("υγρα φρενων", brakeFluid)
        put("ygra frenon", brakeFluid)

        val steeringFluid = RecordCategory.ExpenseCategory.Service.SteeringFluid
        put("steering fluid", steeringFluid)
        put("power steering fluid", steeringFluid)
        put("υγρα τιμονιου", steeringFluid)
        put("ygra timoniou", steeringFluid)

        val valveAdj = RecordCategory.ExpenseCategory.Service.ValveAdjustment
        put("valve adjustment", valveAdj)
        put("ρυθμιση βαλβιδων", valveAdj)
        put("ry8misi valvidon", valveAdj)

        // Sensors
        val sensorsGen = RecordCategory.ExpenseCategory.Service.SensorsGeneral
        put("sensors", sensorsGen)
        put("sensor", sensorsGen)
        put("αισθητηρες", sensorsGen)
        put("aisthitires", sensorsGen)

        // Generic services
        val generalService = RecordCategory.ExpenseCategory.Service.GeneralService
        val smallService = RecordCategory.ExpenseCategory.Service.SmallService
        val largeService = RecordCategory.ExpenseCategory.Service.LargeService

        put("service", generalService)
        put("servis", generalService)
        put("service car", generalService)
        put("γενικο service", generalService)
        put("mikro service", smallService)
        put("small service", smallService)
        put("megalos service", largeService)
        put("big service", largeService)
        put("major service", largeService)

        // --------------------------------------------------
        // 🛞 Tires / Wheels
        // --------------------------------------------------
        val tirePurchase = RecordCategory.ExpenseCategory.Tires.TirePurchase
        put("tires", tirePurchase)
        put("tyres", tirePurchase)
        put("tire", tirePurchase)
        put("tyre", tirePurchase)
        put("new tires", tirePurchase)
        put("allagi elastikon", tirePurchase)
        put("αγορα ελαστικων", tirePurchase)
        put("ελαστικα", tirePurchase)
        put("λαστιχα", tirePurchase)
        put("lastixa", tirePurchase)
        put("elastika", tirePurchase)

        val balancing = RecordCategory.ExpenseCategory.Tires.WheelBalancing
        put("wheel balancing", balancing)
        put("ζυγοσταθμιση", balancing)
        put("zygostathmisi", balancing)

        val alignment = RecordCategory.ExpenseCategory.Tires.WheelAlignment
        put("alignment", alignment)
        put("wheel alignment", alignment)
        put("ευθυγραμμιση", alignment)
        put("efthigramisi", alignment)

        val tireRepair = RecordCategory.ExpenseCategory.Tires.TireRepair
        put("tire repair", tireRepair)
        put("επισκευη ελαστικου", tireRepair)
        put("episkevi lastixou", tireRepair)
        put("voutsa lastixou", tireRepair)

        val rims = RecordCategory.ExpenseCategory.Tires.Rims
        put("rims", rims)
        put("ζαντες", rims)
        put("zantes", rims)
        put("alloy wheels", rims)

        val seasonal = RecordCategory.ExpenseCategory.Tires.SeasonalTireChange
        put("seasonal tires", seasonal)
        put("winter tires", seasonal)
        put("summer tires", seasonal)
        put("allagi epochiakon", seasonal)

        // --------------------------------------------------
        // 🧰 Repairs
        // --------------------------------------------------
        val brakes = RecordCategory.ExpenseCategory.Repairs.Brakes
        put("brakes", brakes)
        put("brake pads", brakes)
        put("pads", brakes)
        put("τακακια", brakes)
        put("discs", brakes)
        put("δισκοι", brakes)
        put("diskoi", brakes)

        val shocks = RecordCategory.ExpenseCategory.Repairs.ShockAbsorbers
        put("shocks", shocks)
        put("shock absorbers", shocks)
        put("amortiser", shocks)
        put("amortiser", shocks)
        put("αμορτισερ", shocks)
        put("amortiser", shocks)

        val battery = RecordCategory.ExpenseCategory.Repairs.Battery
        put("battery", battery)
        put("μπαταρια", battery)
        put("mpataria", battery)
        put("car battery", battery)

        val starter = RecordCategory.ExpenseCategory.Repairs.Starter
        put("starter", starter)
        put("μιζα", starter)
        put("miza", starter)

        val alternator = RecordCategory.ExpenseCategory.Repairs.Alternator
        put("alternator", alternator)
        put("δυναμο", alternator)
        put("dynamo", alternator)

        val clutch = RecordCategory.ExpenseCategory.Repairs.Clutch
        put("clutch", clutch)
        put("συμπλεκτης", clutch)
        put("symplektis", clutch)

        val gearbox = RecordCategory.ExpenseCategory.Repairs.Gearbox
        put("gearbox", gearbox)
        put("κιβωτιο ταχυτητων", gearbox)
        put("kivotio taxititon", gearbox)
        put("σασμαν", gearbox)
        put("sasman", gearbox)

        val exhaust = RecordCategory.ExpenseCategory.Repairs.Exhaust
        put("exhaust", exhaust)
        put("εξατμιση", exhaust)
        put("eksatmisi", exhaust)

        val radiator = RecordCategory.ExpenseCategory.Repairs.Radiator
        put("radiator", radiator)
        put("ψυγειο", radiator)
        put("psigeio", radiator)

        val ac = RecordCategory.ExpenseCategory.Repairs.ACService
        put("ac service", ac)
        put("a/c service", ac)
        put("service ac", ac)
        put("κλιματιστικο service", ac)
        put("service klima", ac)
        put("freon", ac)

        val electrical = RecordCategory.ExpenseCategory.Repairs.ElectricalIssues
        put("electrical", electrical)
        put("electric problem", electrical)
        put("ηλεκτρικα", electrical)
        put("ilektrika", electrical)

        val fuelPump = RecordCategory.ExpenseCategory.Repairs.FuelPump
        put("fuel pump", fuelPump)
        put("τρομπα καυσιμου", fuelPump)
        put("trompa kafsimou", fuelPump)

        val turbo = RecordCategory.ExpenseCategory.Repairs.Turbo
        put("turbo", turbo)
        put("τουρμπινα", turbo)
        put("tourmpina", turbo)

        val driveShaft = RecordCategory.ExpenseCategory.Repairs.DriveShafts
        put("drive shaft", driveShaft)
        put("ημιαξονιο", driveShaft)
        put("imiaxonio", driveShaft)

        val steeringRack = RecordCategory.ExpenseCategory.Repairs.SteeringRack
        put("steering rack", steeringRack)
        put("κρεμαγιερα", steeringRack)
        put("kremagiera", steeringRack)

        val windows = RecordCategory.ExpenseCategory.Repairs.Windows
        put("window motor", windows)
        put("παραθυρα", windows)
        put("parathyra", windows)
        put("parathyro", windows)

        val multimedia = RecordCategory.ExpenseCategory.Repairs.Multimedia
        put("radio", multimedia)
        put("multimedia", multimedia)
        put("stereo", multimedia)
        put("ηχεια", multimedia)
        put("head unit", multimedia)

        val wipers = RecordCategory.ExpenseCategory.Repairs.Wipers
        put("wipers", wipers)
        put("υαλοκαθαριστηρες", wipers)
        put("yalokatharistires", wipers)

        // --------------------------------------------------
        // 📑 Legal / Τέλη / Ασφάλεια
        // --------------------------------------------------
        val insurance = RecordCategory.ExpenseCategory.Legal.Insurance
        put("insurance", insurance)
        put("ασφαλεια", insurance)
        put("asfaleia", insurance)
        put("ασφαλιστρο", insurance)
        put("asfalistro", insurance)

        val tolls = RecordCategory.ExpenseCategory.Legal.Tolls
        put("tolls", tolls)
        put("διοδια", tolls)
        put("diodia", tolls)

        val roadTax = RecordCategory.ExpenseCategory.Legal.RoadTax
        put("road tax", roadTax)
        put("τελη κυκλοφοριας", roadTax)
        put("teli kikloforias", roadTax)

        val kteo = RecordCategory.ExpenseCategory.Legal.KteoMot
        put("kteo", kteo)
        put("m o t", kteo)
        put("mot", kteo)
        put("ελεγχος kteo", kteo)

        val licensePlates = RecordCategory.ExpenseCategory.Legal.LicensePlates
        put("license plates", licensePlates)
        put("plate", licensePlates)
        put("plates", licensePlates)
        put("πινακιδες", licensePlates)
        put("pinakides", licensePlates)

        val fine = RecordCategory.ExpenseCategory.Legal.Fines
        put("fine", fine)
        put("προστιμο", fine)
        put("prostimo", fine)
        put("klisi", fine)

        val regFees = RecordCategory.ExpenseCategory.Legal.RegistrationFees
        put("registration fees", regFees)
        put("τελη ταξινομησης", regFees)
        put("teli taxinomisis", regFees)

        // --------------------------------------------------
        // 🚙 Operational costs
        // --------------------------------------------------
        val carWash = RecordCategory.ExpenseCategory.Operational.CarWash
        put("car wash", carWash)
        put("πλυσιμο", carWash)
        put("plysimo", carWash)
        put("πλυσιμο αυτοκινητου", carWash)

        val intClean = RecordCategory.ExpenseCategory.Operational.InteriorCleaning
        put("interior cleaning", intClean)
        put("εσωτερικος καθαρισμος", intClean)
        put("esoterikos katharismos", intClean)

        val bioClean = RecordCategory.ExpenseCategory.Operational.BiologicalCleaning
        put("biological cleaning", bioClean)
        put("βιολογικος καθαρισμος", bioClean)
        put("viologikos katharismos", bioClean)

        val parking = RecordCategory.ExpenseCategory.Operational.Parking
        put("parking", parking)
        put("park", parking)
        put("παρκινγκ", parking)
        put("parkin", parking)

        val parkingSub = RecordCategory.ExpenseCategory.Operational.ParkingSubscription
        put("parking subscription", parkingSub)
        put("μηνιαιο παρκινγκ", parkingSub)
        put("miniaio parking", parkingSub)

        val roadside = RecordCategory.ExpenseCategory.Operational.RoadsideAssistance
        put("roadside assistance", roadside)
        put("οδικη βοηθεια", roadside)
        put("odiki voithia", roadside)

        val accessories = RecordCategory.ExpenseCategory.Operational.Accessories
        put("accessories", accessories)
        put("αξεσουαρ", accessories)
        put("aksesouar", accessories)

        val dashcamAcc = RecordCategory.ExpenseCategory.Operational.Dashcam
        put("dashcam", dashcamAcc)
        put("camera car", dashcamAcc)

        val gpsTrack = RecordCategory.ExpenseCategory.Operational.GpsTracker
        put("gps tracker", gpsTrack)
        put("tracker", gpsTrack)

        val cleaningSup = RecordCategory.ExpenseCategory.Operational.CleaningSupplies
        put("cleaning supplies", cleaningSup)
        put("καθαριστικα", cleaningSup)
        put("katharistika", cleaningSup)

        // --------------------------------------------------
        // ⚡ EV Special
        // --------------------------------------------------
        val wallbox = RecordCategory.ExpenseCategory.EVSpecial.Wallbox
        put("wallbox", wallbox)
        put("wall box", wallbox)

        val chargerInstall = RecordCategory.ExpenseCategory.EVSpecial.ChargerInstallation
        put("charger installation", chargerInstall)
        put("εγκατασταση φορτιστη", chargerInstall)
        put("egkatastasi fortisti", chargerInstall)

        val chargingSub = RecordCategory.ExpenseCategory.EVSpecial.ChargingSubscription
        put("charging subscription", chargingSub)
        put("συνδρομη φορτισης", chargingSub)

        val cables = RecordCategory.ExpenseCategory.EVSpecial.CablesAdapters
        put("cable", cables)
        put("charging cable", cables)
        put("adapters", cables)
        put("καλωδια φορτισης", cables)

        val battCool = RecordCategory.ExpenseCategory.EVSpecial.BatteryCoolingService
        put("battery cooling", battCool)
        put("service συστηματος ψυξης μπαταριας", battCool)

        // --------------------------------------------------
        // 💥 Damages / Ζημιές
        // --------------------------------------------------
        val bodywork = RecordCategory.ExpenseCategory.Damages.BodyworkPaint
        put("bodywork", bodywork)
        put("panel beating", bodywork)
        put("φανοποιια", bodywork)
        put("βαφη", bodywork)
        put("vafi", bodywork)

        val windshieldRep = RecordCategory.ExpenseCategory.Damages.WindshieldReplacement
        put("windshield", windshieldRep)
        put("παρμπριζ", windshieldRep)
        put("parmpriz", windshieldRep)
        put("allagi parmpriz", windshieldRep)

        val windowRep = RecordCategory.ExpenseCategory.Damages.WindowReplacement
        put("window replacement", windowRep)
        put("allarxi parathyron", windowRep)

        val bumpers = RecordCategory.ExpenseCategory.Damages.BumpersPlastics
        put("bumper", bumpers)
        put("bumpers", bumpers)
        put("προφυλακτηρας", bumpers)
        put("profylaktiras", bumpers)
        put("plastika", bumpers)

        val intDamage = RecordCategory.ExpenseCategory.Damages.InteriorDamages
        put("interior damage", intDamage)
        put("ζημια σαλονιου", intDamage)
        put("zimia saloniou", intDamage)
        put("eksoflisi esoterikou", intDamage)
    }
    /**
     * Όλα τα keywords που γνωρίζει το λεξικό (normalized).
     * Ιδανικά για suggestions / search.
     */
    fun allKeywords(): List<String> =
        keywordToCategory.keys
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()


}
