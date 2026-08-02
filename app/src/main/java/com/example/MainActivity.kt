package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

// Language Enum
enum class AppLanguage {
  ENGLISH, SINHALA
}

// Listing Model
data class CropListing(
  val id: String = UUID.randomUUID().toString(),
  val cropNameEn: String,
  val cropNameSi: String,
  val quantity: String,
  val pricePerKg: String,
  val districtEn: String,
  val districtSi: String,
  val imageResId: Int? = null,
  val iconEmoji: String = "🌾",
  var isSold: Boolean = false,
  val datePosted: String = "Today",
  val farmerName: String = "Sunil Perera (ගොවි මහතා)",
  val farmerPhone: String = "077 345 6789",
  val farmerLocation: String = "Dambulla Center"
)

// Market Crop Price Model for Google Search Market Tracker
enum class PriceTrend {
  UP, DOWN, STABLE
}

data class MarketCropPrice(
  val id: String = UUID.randomUUID().toString(),
  val cropNameEn: String,
  val cropNameSi: String,
  val iconEmoji: String,
  val avgWholesaleLkr: Int,
  val minLkr: Int,
  val maxLkr: Int,
  val trend: PriceTrend,
  val trendTextEn: String,
  val trendTextSi: String,
  val marketCenterEn: String = "Dambulla DEC",
  val marketCenterSi: String = "දඹුල්ල ආර්ථික මධ්‍යස්ථානය"
)

// Initial Wholesale Market Prices sourced via Google Search DEC market data
val INITIAL_MARKET_PRICES = listOf(
  MarketCropPrice("1", "Tomato", "තක්කාලි", "🍅", 320, 290, 350, PriceTrend.UP, "+Rs. 20 (Up)", "+රු. 20 (වැඩිවී ඇත)", "Dambulla DEC", "දඹුල්ල ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("2", "Potato", "අර්තාපල්", "🥔", 280, 250, 310, PriceTrend.DOWN, "-Rs. 10 (Down)", "-රු. 10 (අඩුවී ඇත)", "Nuwara Eliya DEC", "නුවරඑළිය ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("3", "Pumpkin", "වට්ටක්කා", "🎃", 150, 130, 170, PriceTrend.STABLE, "Stable", "ස්ථාවරයි", "Thambuttegama DEC", "තම්බුත්තේගම ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("4", "Carrot", "කැරට්", "🥕", 380, 350, 410, PriceTrend.UP, "+Rs. 15 (Up)", "+රු. 15 (වැඩිවී ඇත)", "Nuwara Eliya DEC", "නුවරඑළිය ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("5", "Green Chili", "අමු මිරිස්", "🌶️", 450, 400, 500, PriceTrend.DOWN, "-Rs. 25 (Down)", "-රු. 25 (අඩුවී ඇත)", "Dambulla DEC", "දඹුල්ල ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("6", "Red Onion", "රතු ලූණු", "🧅", 420, 390, 450, PriceTrend.UP, "+Rs. 10 (Up)", "+රු. 10 (වැඩිවී ඇත)", "Dambulla DEC", "දඹුල්ල ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("7", "Beans", "බෝංචි", "🫛", 290, 260, 320, PriceTrend.STABLE, "Stable", "ස්ථාවරයි", "Dambulla DEC", "දඹුල්ල ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("8", "Brinjal", "වම්බටු", "🍆", 220, 190, 250, PriceTrend.DOWN, "-Rs. 15 (Down)", "-රු. 15 (අඩුවී ඇත)", "Dambulla DEC", "දඹුල්ල ආර්ථික මධ්‍යස්ථානය"),
  MarketCropPrice("9", "Paddy / Rice", "වී / සහල්", "🌾", 125, 115, 135, PriceTrend.UP, "+Rs. 5 (Up)", "+රු. 5 (වැඩිවී ඇත)", "Polonnaruwa DEC", "පොළොන්නරුව ආර්ථික මධ්‍යස්ථානය")
)

// Preset Crop Data
data class PresetCrop(
  val nameEn: String,
  val nameSi: String,
  val iconEmoji: String,
  val defaultPrice: String
)

val PRESET_CROPS = listOf(
  PresetCrop("Tomato", "තක්කාලි", "🍅", "320"),
  PresetCrop("Potato", "අර්තාපල්", "🥔", "280"),
  PresetCrop("Pumpkin", "වට්ටක්කා", "🎃", "150"),
  PresetCrop("Carrot", "කැරට්", "🥕", "380"),
  PresetCrop("Green Chili", "අමු මිරිස්", "🌶️", "450"),
  PresetCrop("Red Onion", "රතු ලූණු", "🧅", "420"),
  PresetCrop("Beans", "බෝංචි", "🫛", "290"),
  PresetCrop("Paddy / Rice", "වී / සහල්", "🌾", "120"),
  PresetCrop("Brinjal", "වම්බටු", "🍆", "220")
)

data class District(
  val nameEn: String,
  val nameSi: String
)

val SRI_LANKA_DISTRICTS = listOf(
  District("Dambulla", "දඹුල්ල"),
  District("Thambuttegama", "තම්බුත්තේගම"),
  District("Nuwara Eliya", "නුවරඑළිය"),
  District("Jaffna", "යාපනය"),
  District("Badulla", "බදුල්ල"),
  District("Kurunegala", "කුරුණෑගල"),
  District("Anuradhapura", "අනුරාධපුරය"),
  District("Ratnapura", "රත්නපුරය"),
  District("Kandy", "මහනුවර"),
  District("Hambantota", "හම්බන්තොට")
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AgriMarketApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriMarketApp() {
  var currentLanguage by remember { mutableStateOf(AppLanguage.SINHALA) }
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Add Crop, 1: Market Prices, 2: Buy Crops, 3: My Listings

  // Shared state for pre-filling Add Crop form from Market Prices
  var prefillCropNameEn by remember { mutableStateOf("Tomato") }
  var prefillCropNameSi by remember { mutableStateOf("තක්කාලි") }
  var prefillEmoji by remember { mutableStateOf("🍅") }
  var prefillPrice by remember { mutableStateOf("320") }

  // Initial Sample Listings for Sri Lankan Farmers
  val listings = remember {
    mutableStateListOf(
      CropListing(
        cropNameEn = "Tomato",
        cropNameSi = "තක්කාලි",
        quantity = "500 Kg",
        pricePerKg = "320",
        districtEn = "Dambulla",
        districtSi = "දඹුල්ල",
        iconEmoji = "🍅",
        imageResId = R.drawable.crop_banner_1785433180576,
        isSold = false,
        datePosted = "Today",
        farmerName = "K. B. Herath (ගොවි මහතා)",
        farmerPhone = "077 123 4567",
        farmerLocation = "Dambulla Economic Center"
      ),
      CropListing(
        cropNameEn = "Carrot",
        cropNameSi = "කැරට්",
        quantity = "400 Kg",
        pricePerKg = "370",
        districtEn = "Nuwara Eliya",
        districtSi = "නුවරඑළිය",
        iconEmoji = "🥕",
        isSold = false,
        datePosted = "Today",
        farmerName = "M. Bandara (ගොවි මහතා)",
        farmerPhone = "075 667 8899",
        farmerLocation = "Kandapola Farm Yard"
      ),
      CropListing(
        cropNameEn = "Pumpkin",
        cropNameSi = "වට්ටක්කා",
        quantity = "1200 Kg",
        pricePerKg = "160",
        districtEn = "Thambuttegama",
        districtSi = "තම්බුත්තේගම",
        iconEmoji = "🎃",
        isSold = false,
        datePosted = "Yesterday",
        farmerName = "P. Jayawardena (ගොවි මහතා)",
        farmerPhone = "071 889 9001",
        farmerLocation = "Thambuttegama Economic Center"
      ),
      CropListing(
        cropNameEn = "Potato",
        cropNameSi = "අර්තාපල්",
        quantity = "300 Kg",
        pricePerKg = "280",
        districtEn = "Nuwara Eliya",
        districtSi = "නුවරඑළිය",
        iconEmoji = "🥔",
        isSold = true,
        datePosted = "2 days ago",
        farmerName = "S. Wickramasinghe (ගොවි මහතා)",
        farmerPhone = "072 445 1122",
        farmerLocation = "Keppetipola Market"
      )
    )
  }

  // Dialog State for Mark as Sold Confirmation
  var listingToMarkSold by remember { mutableStateOf<CropListing?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = ForestGreen,
          titleContentColor = Color.White
        ),
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = GoldenYellow,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "🌾",
                  fontSize = 20.sp
                )
              }
            }
            Column {
              Text(
                text = if (currentLanguage == AppLanguage.SINHALA) "ගොවි පොළ LK" else "AgriMarket LK",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
              )
              Text(
                text = if (currentLanguage == AppLanguage.SINHALA) "ශ්‍රී ලාංකීය ගොවි ජනතාව වෙනුවෙන්" else "Sri Lankan Agricultural Marketplace",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f)
              )
            }
          }
        },
        actions = {
          // Language Switcher Toggle Pill
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = GoldenYellow,
            modifier = Modifier
              .padding(end = 12.dp)
              .clickable {
                currentLanguage = if (currentLanguage == AppLanguage.SINHALA) AppLanguage.ENGLISH else AppLanguage.SINHALA
              }
              .testTag("language_toggle_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language",
                tint = DarkGrayText,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (currentLanguage == AppLanguage.SINHALA) "සිංහල | EN" else "EN | සිංහල",
                color = DarkGrayText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = {
            Icon(
              imageVector = if (selectedTab == 0) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
              contentDescription = "Add Crop"
            )
          },
          label = {
            Text(
              text = if (currentLanguage == AppLanguage.SINHALA) "පළ කරන්න" else "Add Crop",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreen,
            selectedTextColor = ForestGreen,
            indicatorColor = LightForestGreen
          ),
          modifier = Modifier.testTag("nav_add_crop_tab")
        )
        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = {
            Icon(
              imageVector = if (selectedTab == 1) Icons.Filled.ShowChart else Icons.Outlined.ShowChart,
              contentDescription = "Market Prices"
            )
          },
          label = {
            Text(
              text = if (currentLanguage == AppLanguage.SINHALA) "මිල ගණන්" else "Market Prices",
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreen,
            selectedTextColor = ForestGreen,
            indicatorColor = LightForestGreen
          ),
          modifier = Modifier.testTag("nav_market_prices_tab")
        )
        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = {
            Icon(
              imageVector = if (selectedTab == 2) Icons.Filled.Storefront else Icons.Outlined.Storefront,
              contentDescription = "Buy Crops"
            )
          },
          label = {
            Text(
              text = if (currentLanguage == AppLanguage.SINHALA) "මිලදී ගන්න" else "Buy Crops",
              fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreen,
            selectedTextColor = ForestGreen,
            indicatorColor = LightForestGreen
          ),
          modifier = Modifier.testTag("nav_buy_crops_tab")
        )
        NavigationBarItem(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          icon = {
            BadgedBox(
              badge = {
                val activeCount = listings.count { !it.isSold }
                if (activeCount > 0) {
                  Badge(containerColor = GoldenYellow, contentColor = DarkGrayText) {
                    Text(text = activeCount.toString(), fontWeight = FontWeight.Bold)
                  }
                }
              }
            ) {
              Icon(
                imageVector = if (selectedTab == 3) Icons.Filled.ListAlt else Icons.Outlined.ListAlt,
                contentDescription = "My Listings"
              )
            }
          },
          label = {
            Text(
              text = if (currentLanguage == AppLanguage.SINHALA) "මගේ දැන්වීම්" else "My Listings",
              fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ForestGreen,
            selectedTextColor = ForestGreen,
            indicatorColor = LightForestGreen
          ),
          modifier = Modifier.testTag("nav_my_listings_tab")
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(Color(0xFFF7F9F6))
    ) {
      when (selectedTab) {
        0 -> {
          AddCropScreen(
            language = currentLanguage,
            prefillCropNameEn = prefillCropNameEn,
            prefillCropNameSi = prefillCropNameSi,
            prefillEmoji = prefillEmoji,
            prefillPrice = prefillPrice,
            onPublish = { newListing ->
              listings.add(0, newListing)
              selectedTab = 3 // Switch to My Listings automatically
            }
          )
        }
        1 -> {
          MarketPriceTrackerScreen(
            language = currentLanguage,
            onSelectCropToSell = { marketCrop ->
              prefillCropNameEn = marketCrop.cropNameEn
              prefillCropNameSi = marketCrop.cropNameSi
              prefillEmoji = marketCrop.iconEmoji
              prefillPrice = marketCrop.avgWholesaleLkr.toString()
              selectedTab = 0 // Switch to Add Crop tab
            }
          )
        }
        2 -> {
          BuyersMarketplaceScreen(
            language = currentLanguage,
            listings = listings,
            onNavigateToAddCrop = { selectedTab = 0 }
          )
        }
        3 -> {
          MyListingsScreen(
            language = currentLanguage,
            listings = listings,
            onMarkAsSoldRequested = { listing ->
              listingToMarkSold = listing
            }
          )
        }
      }

      // Confirmation Dialog for Mark As Sold
      listingToMarkSold?.let { listing ->
        AlertDialog(
          onDismissRequest = { listingToMarkSold = null },
          icon = {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(36.dp)
            )
          },
          title = {
            Text(
              text = if (currentLanguage == AppLanguage.SINHALA) "අලෙවි වූ බව තහවුරු කරන්න" else "Confirm Mark as Sold",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              textAlign = TextAlign.Center
            )
          },
          text = {
            val name = if (currentLanguage == AppLanguage.SINHALA) listing.cropNameSi else listing.cropNameEn
            val msg = if (currentLanguage == AppLanguage.SINHALA)
              "ඔබේ '$name' දැන්වීම 'අලෙවි විය / SOLD' ලෙස සලකුණු කිරීමට ඔබට විශ්වාසද?"
            else
              "Are you sure you want to mark your listing for '$name' as SOLD?"

            Text(
              text = msg,
              fontSize = 15.sp,
              textAlign = TextAlign.Center
            )
          },
          confirmButton = {
            Button(
              onClick = {
                val index = listings.indexOfFirst { it.id == listing.id }
                if (index != -1) {
                  listings[index] = listings[index].copy(isSold = true)
                }
                listingToMarkSold = null
              },
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
              modifier = Modifier.testTag("confirm_sold_button")
            ) {
              Text(
                text = if (currentLanguage == AppLanguage.SINHALA) "ඔවු, අලෙවි විය" else "Yes, Mark as Sold",
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }
          },
          dismissButton = {
            OutlinedButton(
              onClick = { listingToMarkSold = null },
              modifier = Modifier.testTag("cancel_sold_button")
            ) {
              Text(
                text = if (currentLanguage == AppLanguage.SINHALA) "අවලංගු කරන්න" else "Cancel"
              )
            }
          },
          containerColor = Color.White,
          shape = RoundedCornerShape(20.dp)
        )
      }
    }
  }
}

// SCREEN 1: ADD CROP FORM
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCropScreen(
  language: AppLanguage,
  prefillCropNameEn: String = "Tomato",
  prefillCropNameSi: String = "තක්කාලි",
  prefillEmoji: String = "🍅",
  prefillPrice: String = "320",
  onPublish: (CropListing) -> Unit
) {
  val context = LocalContext.current
  var selectedCropNameEn by remember(prefillCropNameEn) { mutableStateOf(prefillCropNameEn) }
  var selectedCropNameSi by remember(prefillCropNameSi) { mutableStateOf(prefillCropNameSi) }
  var selectedEmoji by remember(prefillEmoji) { mutableStateOf(prefillEmoji) }

  var customCropInput by remember { mutableStateOf("") }
  var isCustomCrop by remember { mutableStateOf(false) }

  var quantityInput by remember { mutableStateOf("") }
  var priceInput by remember(prefillPrice) { mutableStateOf(prefillPrice) }

  var selectedDistrictEn by remember { mutableStateOf("Dambulla") }
  var selectedDistrictSi by remember { mutableStateOf("දඹුල්ල") }

  var farmerNameInput by remember { mutableStateOf("S. Bandara (ගොවි මහතා)") }
  var farmerPhoneInput by remember { mutableStateOf("077 123 4567") }

  var showCropDropdown by remember { mutableStateOf(false) }
  var showDistrictDropdown by remember { mutableStateOf(false) }
  var showVoiceDialog by remember { mutableStateOf(false) }
  var showPhotoPickerSheet by remember { mutableStateOf(false) }
  var selectedPhotoResId by remember { mutableStateOf<Int?>(R.drawable.crop_banner_1785433180576) }

  // State for live market price lookup inside form
  val currentMarketPrice = INITIAL_MARKET_PRICES.find { it.cropNameEn.equals(selectedCropNameEn, ignoreCase = true) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Banner
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = LightForestGreen),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = ForestGreen,
            modifier = Modifier.size(48.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AddBusiness,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (language == AppLanguage.SINHALA) "අස්වැන්න / නිෂ්පාදනය එකතු කරන්න" else "Add Crop / Product",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = DarkForestGreen
            )
            Text(
              text = if (language == AppLanguage.SINHALA) "ගැනුම්කරුවන් අමතන පරිදි විස්තර සපයන්න" else "Fill in crop details to reach buyers across Sri Lanka",
              fontSize = 12.sp,
              color = DarkForestGreen.copy(alpha = 0.8f)
            )
          }
        }
      }
    }

    // 1. IMAGE PICKER PLACEHOLDER BUTTON
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = if (language == AppLanguage.SINHALA) "1. ඡායාරූපය (Photo)" else "1. Product Photo",
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          color = DarkGrayText
        )

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(2.dp, ForestGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { showPhotoPickerSheet = true }
            .testTag("crop_image_picker_button")
        ) {
          if (selectedPhotoResId != null) {
            Box(modifier = Modifier.fillMaxSize()) {
              Image(
                painter = painterResource(id = selectedPhotoResId!!),
                contentDescription = "Crop photo preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
              Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = if (language == AppLanguage.SINHALA) "ඡායාරූපය වෙනස් කරන්න" else "Change Photo",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          } else {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Surface(
                shape = CircleShape,
                color = GoldenYellow.copy(alpha = 0.3f),
                modifier = Modifier.size(56.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(28.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = if (language == AppLanguage.SINHALA) "ඡායාරූපයක් එක් කරන්න (Add Photo)" else "Tap to Select / Take Crop Photo",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ForestGreen
              )
              Text(
                text = if (language == AppLanguage.SINHALA) "හොඳ ඡායාරූපයකින් ඉක්මන් අලෙවියක් ලැබෙයි" else "High quality photos attract more buyers",
                fontSize = 11.sp,
                color = MediumGrayText
              )
            }
          }
        }
      }
    }

    // 2. CROP NAME DROPDOWN / INPUT
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = if (language == AppLanguage.SINHALA) "2. බෝගයේ නම (Crop Name)" else "2. Crop Name",
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          color = DarkGrayText
        )

        ExposedDropdownMenuBox(
          expanded = showCropDropdown,
          onExpandedChange = { showCropDropdown = !showCropDropdown }
        ) {
          OutlinedTextField(
            value = if (isCustomCrop) customCropInput else "$selectedEmoji ${if (language == AppLanguage.SINHALA) selectedCropNameSi else selectedCropNameEn}",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCropDropdown) },
            leadingIcon = {
              Text(text = selectedEmoji, fontSize = 20.sp)
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ForestGreen,
              unfocusedBorderColor = Color.LightGray,
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
              .testTag("crop_name_dropdown")
          )

          ExposedDropdownMenu(
            expanded = showCropDropdown,
            onDismissRequest = { showCropDropdown = false }
          ) {
            PRESET_CROPS.forEach { crop ->
              DropdownMenuItem(
                text = {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text(text = crop.iconEmoji, fontSize = 18.sp)
                    Text(
                      text = if (language == AppLanguage.SINHALA) crop.nameSi else crop.nameEn,
                      fontWeight = FontWeight.Medium
                    )
                  }
                },
                onClick = {
                  isCustomCrop = false
                  selectedCropNameEn = crop.nameEn
                  selectedCropNameSi = crop.nameSi
                  selectedEmoji = crop.iconEmoji
                  priceInput = crop.defaultPrice
                  showCropDropdown = false
                }
              )
            }
          }
        }
      }
    }

    // 3. QUANTITY INPUT FIELD (with mic icon for voice note input)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = if (language == AppLanguage.SINHALA) "3. ප්‍රමාණය - කි.ග්‍රෑ. / ඒකක (Quantity)" else "3. Quantity (in Kg / Units)",
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          color = DarkGrayText
        )

        OutlinedTextField(
          value = quantityInput,
          onValueChange = { quantityInput = it },
          placeholder = {
            Text(
              text = if (language == AppLanguage.SINHALA) "උදා: 500 Kg හෝ 1000" else "e.g., 500 Kg or 50 Bags"
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Outlined.Scale,
              contentDescription = null,
              tint = ForestGreen
            )
          },
          trailingIcon = {
            // Prominent Mic Icon Button for Voice Input
            Surface(
              shape = CircleShape,
              color = GoldenYellow,
              modifier = Modifier
                .padding(end = 4.dp)
                .size(40.dp)
                .clickable { showVoiceDialog = true }
                .testTag("voice_input_mic_button")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Voice Input",
                  tint = DarkGrayText,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("quantity_input_field")
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.RecordVoiceOver,
            contentDescription = null,
            tint = DarkGold,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = if (language == AppLanguage.SINHALA) "හඬින් ඇතුළත් කිරීමට මයික්‍රෆෝනය ඔබන්න" else "Tap golden mic icon for voice dictation",
            fontSize = 11.sp,
            color = DarkGold,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // MARKET PRICE TRACKER SUMMARY CARD INSIDE FORM
    currentMarketPrice?.let { marketData ->
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LightGold),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldenYellow),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("form_market_price_tracker_card")
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.TravelExplore,
                  contentDescription = null,
                  tint = DarkForestGreen,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = if (language == AppLanguage.SINHALA) "ගූගල් සෙවුම් වෙළඳපොළ මිල (Google Search Tracker)" else "Google Search Market Rate Tracker",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = DarkForestGreen
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GoldenYellow
              ) {
                Text(
                  text = if (language == AppLanguage.SINHALA) "සජීවී" else "LIVE",
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = DarkGrayText,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "${if (language == AppLanguage.SINHALA) marketData.marketCenterSi else marketData.marketCenterEn} • Today",
                  fontSize = 11.sp,
                  color = MediumGrayText
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "LKR ${marketData.avgWholesaleLkr} / kg",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ForestGreen
                  )
                  Text(
                    text = "(Range: ${marketData.minLkr}-${marketData.maxLkr})",
                    fontSize = 11.sp,
                    color = Color.Gray
                  )
                }
              }

              Button(
                onClick = {
                  priceInput = marketData.avgWholesaleLkr.toString()
                  Toast.makeText(
                    context,
                    if (language == AppLanguage.SINHALA) "තෝරාගත් බෝගයට අද වෙළඳපොළ මිල (රු. ${marketData.avgWholesaleLkr}) යොදන ලදී" else "Applied market wholesale rate LKR ${marketData.avgWholesaleLkr}",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text(
                  text = if (language == AppLanguage.SINHALA) "මෙම මිල යොදන්න" else "Use Market Rate",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }
        }
      }
    }

    // 4. PRICE PER KG INPUT FIELD (in LKR)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = if (language == AppLanguage.SINHALA) "4. කිලෝවක/ඒකකයක මිල - රු. (Price per Kg in LKR)" else "4. Price per Kg / Unit (in LKR)",
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          color = DarkGrayText
        )

        OutlinedTextField(
          value = priceInput,
          onValueChange = { priceInput = it.filter { char -> char.isDigit() || char == '.' } },
          placeholder = {
            Text(text = "e.g., 320")
          },
          prefix = {
            Text(
              text = if (language == AppLanguage.SINHALA) "රු. " else "LKR ",
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Outlined.Payments,
              contentDescription = null,
              tint = ForestGreen
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("price_input_field")
        )
      }
    }

    // 5. LOCATION / DISTRICT DROPDOWN
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = if (language == AppLanguage.SINHALA) "5. ස්ථානය / දිස්ත්‍රික්කය (Location)" else "5. Location / District",
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          color = DarkGrayText
        )

        ExposedDropdownMenuBox(
          expanded = showDistrictDropdown,
          onExpandedChange = { showDistrictDropdown = !showDistrictDropdown }
        ) {
          OutlinedTextField(
            value = if (language == AppLanguage.SINHALA) selectedDistrictSi else selectedDistrictEn,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDistrictDropdown) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = ForestGreen
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ForestGreen,
              unfocusedBorderColor = Color.LightGray,
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
              .testTag("location_district_dropdown")
          )

          ExposedDropdownMenu(
            expanded = showDistrictDropdown,
            onDismissRequest = { showDistrictDropdown = false }
          ) {
            SRI_LANKA_DISTRICTS.forEach { district ->
              DropdownMenuItem(
                text = {
                  Text(
                    text = if (language == AppLanguage.SINHALA) district.nameSi else district.nameEn,
                    fontWeight = FontWeight.Medium
                  )
                },
                onClick = {
                  selectedDistrictEn = district.nameEn
                  selectedDistrictSi = district.nameSi
                  showDistrictDropdown = false
                }
              )
            }
          }
        }
      }
    }

    // 6. FARMER CONTACT INFO (FARMER NAME & PHONE)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = if (language == AppLanguage.SINHALA) "6. ගොවි මහතාගේ විස්තර (Farmer Profile & Phone)" else "6. Farmer Profile & Contact",
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          color = DarkGrayText
        )

        OutlinedTextField(
          value = farmerNameInput,
          onValueChange = { farmerNameInput = it },
          label = {
            Text(if (language == AppLanguage.SINHALA) "ගොවි මහතාගේ නම (Farmer Name)" else "Farmer Name")
          },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ForestGreen)
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("farmer_name_input_field")
        )

        OutlinedTextField(
          value = farmerPhoneInput,
          onValueChange = { farmerPhoneInput = it },
          label = {
            Text(if (language == AppLanguage.SINHALA) "දුරකථන අංකය (Phone Number)" else "Phone Number")
          },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = ForestGreen)
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("farmer_phone_input_field")
        )
      }
    }

    // 7. LARGE GREEN "PUBLISH / පළ කරන්න" BUTTON
    item {
      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = {
          if (quantityInput.isBlank() || priceInput.isBlank()) {
            Toast.makeText(
              context,
              if (language == AppLanguage.SINHALA) "කරුණාකර ප්‍රමාණය සහ මිල ඇතුළත් කරන්න" else "Please enter quantity and price",
              Toast.LENGTH_SHORT
            ).show()
            return@Button
          }

          val cropEn = if (isCustomCrop) customCropInput else selectedCropNameEn
          val cropSi = if (isCustomCrop) customCropInput else selectedCropNameSi

          val newListing = CropListing(
            cropNameEn = cropEn,
            cropNameSi = cropSi,
            quantity = if (quantityInput.lowercase().contains("kg") || quantityInput.lowercase().contains("කි.ග්‍රෑ")) quantityInput else "$quantityInput Kg",
            pricePerKg = priceInput,
            districtEn = selectedDistrictEn,
            districtSi = selectedDistrictSi,
            iconEmoji = selectedEmoji,
            imageResId = selectedPhotoResId,
            isSold = false,
            datePosted = if (language == AppLanguage.SINHALA) "දැන්" else "Just now",
            farmerName = farmerNameInput.ifBlank { "K. B. Herath (ගොවි මහතා)" },
            farmerPhone = farmerPhoneInput.ifBlank { "077 123 4567" },
            farmerLocation = "${if (language == AppLanguage.SINHALA) selectedDistrictSi else selectedDistrictEn} Center"
          )

          onPublish(newListing)
          Toast.makeText(
            context,
            if (language == AppLanguage.SINHALA) "දැන්වීම සාර්ථකව පළ විය!" else "Listing Published Successfully!",
            Toast.LENGTH_LONG
          ).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp)
          .testTag("publish_listing_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = if (language == AppLanguage.SINHALA) "පළ කරන්න (Publish Listing)" else "Publish Listing",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Voice Note Dialog Simulator
  if (showVoiceDialog) {
    AlertDialog(
      onDismissRequest = { showVoiceDialog = false },
      icon = {
        Surface(
          shape = CircleShape,
          color = GoldenYellow,
          modifier = Modifier.size(64.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = null,
              tint = DarkGrayText,
              modifier = Modifier.size(32.dp)
            )
          }
        }
      },
      title = {
        Text(
          text = if (language == AppLanguage.SINHALA) "හඬින් ප්‍රමාණය කියන්න" else "Voice Input - Speak Quantity",
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      },
      text = {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = if (language == AppLanguage.SINHALA) "සවන්දෙමින් පවතී... (Listening...)\nඋදා: 'කිලෝ 500ක්' හෝ '500 Kg'" else "Listening... Speak naturally, e.g., '500 Kg' or '100 Bags'",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MediumGrayText
          )

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (language == AppLanguage.SINHALA) "තෝරන්න (Quick Select):" else "Quick Voice Presets:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGrayText
          )

          // Quick Presets for Sri Lankan farmers
          val voicePresets = listOf("100 Kg", "250 Kg", "500 Kg", "1000 Kg")
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            voicePresets.forEach { preset ->
              FilterChip(
                selected = false,
                onClick = {
                  quantityInput = preset
                  showVoiceDialog = false
                  Toast.makeText(context, "Voice input: $preset", Toast.LENGTH_SHORT).show()
                },
                label = { Text(preset, fontSize = 12.sp) }
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (quantityInput.isBlank()) {
              quantityInput = "500 Kg"
            }
            showVoiceDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
        ) {
          Text(
            text = if (language == AppLanguage.SINHALA) "හරි (Done)" else "Apply Voice Input",
            fontWeight = FontWeight.Bold
          )
        }
      },
      dismissButton = {
        TextButton(onClick = { showVoiceDialog = false }) {
          Text(text = if (language == AppLanguage.SINHALA) "අවලංගු කරන්න" else "Cancel")
        }
      },
      containerColor = Color.White,
      shape = RoundedCornerShape(20.dp)
    )
  }

  // Photo Picker Modal Sheet
  if (showPhotoPickerSheet) {
    AlertDialog(
      onDismissRequest = { showPhotoPickerSheet = false },
      title = {
        Text(
          text = if (language == AppLanguage.SINHALA) "ඡායාරූපයක් තෝරන්න" else "Select Crop Photo",
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = if (language == AppLanguage.SINHALA) "ඔබේ අස්වැන්නේ පැහැදිලි ඡායාරූපයක් භාවිතා කරන්න" else "Choose a clear photo for your marketplace listing",
            fontSize = 13.sp,
            color = MediumGrayText
          )

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LightForestGreen),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                selectedPhotoResId = R.drawable.crop_banner_1785433180576
                showPhotoPickerSheet = false
              }
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Image(
                painter = painterResource(id = R.drawable.crop_banner_1785433180576),
                contentDescription = null,
                modifier = Modifier
                  .size(54.dp)
                  .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
              )
              Column {
                Text(
                  text = if (language == AppLanguage.SINHALA) "නැවුම් අස්වැන්න (Sample Fresh Produce)" else "Sample Fresh Farm Harvest",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = if (language == AppLanguage.SINHALA) "ඉහළ තත්ත්වයේ ඡායාරූපය" else "High quality harvest photo",
                  fontSize = 11.sp,
                  color = MediumGrayText
                )
              }
            }
          }

          OutlinedButton(
            onClick = {
              selectedPhotoResId = null
              showPhotoPickerSheet = false
            },
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (language == AppLanguage.SINHALA) "සංකේතය පමනක් භාවිතා කරන්න" else "Use Default Icon Only")
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showPhotoPickerSheet = false }) {
          Text(text = if (language == AppLanguage.SINHALA) "වසා දමන්න" else "Close")
        }
      },
      containerColor = Color.White,
      shape = RoundedCornerShape(20.dp)
    )
  }
}

// SCREEN 2: MARKET PRICE TRACKER SCREEN (Google Search Wholesale Tracker)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPriceTrackerScreen(
  language: AppLanguage,
  onSelectCropToSell: (MarketCropPrice) -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var isSyncing by remember { mutableStateOf(false) }
  var lastUpdatedTime by remember { mutableStateOf("Today, 31 July 2026") }
  var searchQuery by remember { mutableStateOf("") }
  var selectedMarketFilter by remember { mutableStateOf("All Markets") }

  var marketPrices by remember { mutableStateOf(INITIAL_MARKET_PRICES) }

  val marketOptions = listOf(
    "All Markets",
    "Dambulla DEC",
    "Nuwara Eliya DEC",
    "Thambuttegama DEC",
    "Polonnaruwa DEC"
  )

  val filteredPrices = marketPrices.filter { price ->
    val matchesSearch = searchQuery.isBlank() ||
      price.cropNameEn.contains(searchQuery, ignoreCase = true) ||
      price.cropNameSi.contains(searchQuery, ignoreCase = true)

    val matchesMarket = selectedMarketFilter == "All Markets" ||
      price.marketCenterEn.contains(selectedMarketFilter, ignoreCase = true)

    matchesSearch && matchesMarket
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // TOP FEATURED HERO BANNER CARD
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkForestGreen),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = CircleShape,
                color = GoldenYellow,
                modifier = Modifier.size(32.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.TravelExplore,
                    contentDescription = "Google Search",
                    tint = DarkGrayText,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
              Column {
                Text(
                  text = if (language == AppLanguage.SINHALA) "තොග වෙළඳපොළ මිල ගණන්" else "Market Wholesale Price Tracker",
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  color = Color.White
                )
                Text(
                  text = if (language == AppLanguage.SINHALA) "ගූගල් සෙවුම් සහ මධ්‍යස්ථාන දත්ත හරහා" else "Google Search Wholesale Market Feed",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.8f)
                )
              }
            }

            // Live Sync Refresh Button
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = GoldenYellow,
              modifier = Modifier
                .clickable(enabled = !isSyncing) {
                  isSyncing = true
                  coroutineScope.launch {
                    Toast.makeText(
                      context,
                      if (language == AppLanguage.SINHALA) "ගූගල් වෙතින් අද දඹුල්ල සහ ප්‍රධාන ආර්ථික මධ්‍යස්ථාන මිල ගණන් සෙවීම..." else "Fetching today's Dambulla & DEC wholesale prices via Google Search...",
                      Toast.LENGTH_SHORT
                    ).show()

                    delay(1200)

                    // Simulate slight live fluctuation update
                    marketPrices = marketPrices.map { item ->
                      val delta = (-10..15).random()
                      item.copy(
                        avgWholesaleLkr = (item.avgWholesaleLkr + delta).coerceAtLeast(50),
                        minLkr = (item.minLkr + delta).coerceAtLeast(40),
                        maxLkr = (item.maxLkr + delta).coerceAtLeast(60)
                      )
                    }

                    isSyncing = false
                    lastUpdatedTime = "Today, 31 July 2026 (Live)"
                    Toast.makeText(
                      context,
                      if (language == AppLanguage.SINHALA) "මිල ගණන් සාර්ථකව යාවත්කාලීන විය!" else "Prices updated successfully via Google Search!",
                      Toast.LENGTH_SHORT
                    ).show()
                  }
                }
                .testTag("refresh_google_prices_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                if (isSyncing) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = DarkGrayText,
                    strokeWidth = 2.dp
                  )
                } else {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync",
                    tint = DarkGrayText,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Text(
                  text = if (language == AppLanguage.SINHALA) "සජීවී යාවත්කාලීන" else "Sync Live",
                  color = DarkGrayText,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }

          Divider(color = Color.White.copy(alpha = 0.2f))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = GoldenYellow,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "$lastUpdatedTime • Dambulla, Nuwara Eliya & DEC",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f)
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.15f)
            ) {
              Text(
                text = "LKR / 1Kg",
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    // SEARCH & MARKET FILTER BAR
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = if (language == AppLanguage.SINHALA) "එළවළු හෝ බෝග සෝයන්න (Search crop...)" else "Search crop by name (e.g. Tomato, කැරට්)..."
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = ForestGreen
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("market_price_search_input")
        )

        // Horizontal Market Filter Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(marketOptions) { market ->
            val isSelected = selectedMarketFilter == market
            FilterChip(
              selected = isSelected,
              onClick = { selectedMarketFilter = market },
              label = {
                Text(
                  text = market,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = LightForestGreen,
                selectedLabelColor = DarkForestGreen,
                containerColor = Color.White
              ),
              border = BorderStroke(1.dp, if (isSelected) ForestGreen else Color.LightGray)
            )
          }
        }
      }
    }

    // LIST OF MARKET CROP PRICES
    if (filteredPrices.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (language == AppLanguage.SINHALA) "ගැලපෙන මිල ගණන් සොයාගත නොහැකි විය" else "No matching market prices found",
            color = MediumGrayText,
            fontSize = 14.sp
          )
        }
      }
    } else {
      items(filteredPrices, key = { it.id }) { marketCrop ->
        MarketCropPriceCard(
          language = language,
          marketCrop = marketCrop,
          onSellAtThisPrice = { onSelectCropToSell(marketCrop) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun MarketCropPriceCard(
  language: AppLanguage,
  marketCrop: MarketCropPrice,
  onSellAtThisPrice: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("market_price_card_${marketCrop.id}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = LightForestGreen,
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = marketCrop.iconEmoji, fontSize = 24.sp)
            }
          }

          Column {
            Text(
              text = if (language == AppLanguage.SINHALA) marketCrop.cropNameSi else marketCrop.cropNameEn,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = DarkGrayText
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = if (language == AppLanguage.SINHALA) marketCrop.marketCenterSi else marketCrop.marketCenterEn,
                fontSize = 11.sp,
                color = MediumGrayText
              )
            }
          }
        }

        // Price Trend Pill
        val trendBg = when (marketCrop.trend) {
          PriceTrend.UP -> Color(0xFFE8F5E9)
          PriceTrend.DOWN -> Color(0xFFFFEBEE)
          PriceTrend.STABLE -> Color(0xFFECEFF1)
        }
        val trendTextColor = when (marketCrop.trend) {
          PriceTrend.UP -> DarkForestGreen
          PriceTrend.DOWN -> Color(0xFFC62828)
          PriceTrend.STABLE -> DarkGrayText
        }
        val trendIcon = when (marketCrop.trend) {
          PriceTrend.UP -> Icons.Default.TrendingUp
          PriceTrend.DOWN -> Icons.Default.TrendingDown
          PriceTrend.STABLE -> Icons.Default.TrendingFlat
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = trendBg
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = trendIcon,
              contentDescription = null,
              tint = trendTextColor,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = if (language == AppLanguage.SINHALA) marketCrop.trendTextSi else marketCrop.trendTextEn,
              color = trendTextColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }

      Divider(color = Color.LightGray.copy(alpha = 0.4f))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (language == AppLanguage.SINHALA) "තොග සාමාන්‍ය මිල" else "Avg Wholesale Rate",
            fontSize = 10.sp,
            color = Color.Gray
          )
          Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "LKR ${marketCrop.avgWholesaleLkr}",
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              color = ForestGreen
            )
            Text(
              text = "/ 1Kg",
              fontSize = 12.sp,
              color = MediumGrayText,
              modifier = Modifier.padding(bottom = 2.dp)
            )
          }
          Text(
            text = "Min: Rs. ${marketCrop.minLkr} - Max: Rs. ${marketCrop.maxLkr}",
            fontSize = 11.sp,
            color = MediumGrayText
          )
        }

        // Quick Sell at this price button
        Button(
          onClick = onSellAtThisPrice,
          colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
          shape = RoundedCornerShape(12.dp),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AddShoppingCart,
              contentDescription = null,
              tint = DarkGrayText,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (language == AppLanguage.SINHALA) "මෙම මිලට පළ කරන්න" else "Sell at this Price",
              color = DarkGrayText,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

// SCREEN 3: MY LISTINGS SCREEN
@Composable
fun MyListingsScreen(
  language: AppLanguage,
  listings: List<CropListing>,
  onMarkAsSoldRequested: (CropListing) -> Unit
) {
  val activeListings = listings.filter { !it.isSold }
  val soldListings = listings.filter { it.isSold }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Summary Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = activeListings.size.toString(),
              fontWeight = FontWeight.Bold,
              fontSize = 24.sp,
              color = ForestGreen
            )
            Text(
              text = if (language == AppLanguage.SINHALA) "සක්‍රීය දැන්වීම්" else "Active Listings",
              fontSize = 12.sp,
              color = MediumGrayText
            )
          }

          Divider(
            modifier = Modifier
              .height(36.dp)
              .width(1.dp),
            color = Color.LightGray
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = soldListings.size.toString(),
              fontWeight = FontWeight.Bold,
              fontSize = 24.sp,
              color = SoldBannerBg
            )
            Text(
              text = if (language == AppLanguage.SINHALA) "අලෙවි වූ අයිතම" else "Sold Items",
              fontSize = 12.sp,
              color = MediumGrayText
            )
          }
        }
      }
    }

    if (listings.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🌾", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (language == AppLanguage.SINHALA) "දැනට කිසිදු දැන්වීමක් නොමැත" else "No listings added yet",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = if (language == AppLanguage.SINHALA) "'පළ කරන්න' ටැබය ක්ලික් කර අස්වැන්න එක් කරන්න" else "Tap 'Add Crop' tab to publish your first crop",
              fontSize = 12.sp,
              color = MediumGrayText
            )
          }
        }
      }
    } else {
      // ACTIVE LISTINGS SECTION
      if (activeListings.isNotEmpty()) {
        item {
          Text(
            text = if (language == AppLanguage.SINHALA) "අලෙවිය සඳහා පවතින අස්වැන්න" else "Active Crops for Sale",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = DarkGrayText
          )
        }

        items(activeListings, key = { it.id }) { listing ->
          CropListingCard(
            language = language,
            listing = listing,
            onMarkAsSoldClicked = { onMarkAsSoldRequested(listing) }
          )
        }
      }

      // SOLD LISTINGS SECTION
      if (soldListings.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (language == AppLanguage.SINHALA) "අලෙවි වූ දැන්වීම් (Sold)" else "Recently Sold Crops",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = SoldBannerBg
          )
        }

        items(soldListings, key = { it.id }) { listing ->
          CropListingCard(
            language = language,
            listing = listing,
            onMarkAsSoldClicked = {}
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun CropListingCard(
  language: AppLanguage,
  listing: CropListing,
  onMarkAsSoldClicked: () -> Unit
) {
  val isSold = listing.isSold
  val cardBgColor = if (isSold) DisabledCardBg else Color.White
  val cardAlpha = if (isSold) 0.65f else 1.0f

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBgColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSold) 0.dp else 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .alpha(cardAlpha)
      .testTag("crop_listing_card_${listing.id}")
  ) {
    Column {
      // Top Status Banner if Sold
      if (isSold) {
        Surface(
          color = SoldBannerBg,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (language == AppLanguage.SINHALA) "අලෙවි විය / SOLD" else "MARKETPLACE STATUS: SOLD",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }

      Row(
        modifier = Modifier.padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Thumbnail Image or Emoji Badge
        if (listing.imageResId != null) {
          Box(
            modifier = Modifier
              .size(86.dp)
              .clip(RoundedCornerShape(12.dp))
          ) {
            Image(
              painter = painterResource(id = listing.imageResId),
              contentDescription = null,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
        } else {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSold) Color.LightGray else LightForestGreen,
            modifier = Modifier.size(86.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = listing.iconEmoji, fontSize = 40.sp)
            }
          }
        }

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (language == AppLanguage.SINHALA) listing.cropNameSi else listing.cropNameEn,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = DarkGrayText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            // Available/Sold Badge
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSold) SoldBannerBg.copy(alpha = 0.2f) else GoldenYellow.copy(alpha = 0.3f)
            ) {
              Text(
                text = if (isSold) (if (language == AppLanguage.SINHALA) "අලෙවි විය" else "SOLD")
                else (if (language == AppLanguage.SINHALA) "ඇත" else "AVAILABLE"),
                color = if (isSold) SoldBannerBg else DarkForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = if (language == AppLanguage.SINHALA) listing.districtSi else listing.districtEn,
              fontSize = 13.sp,
              color = MediumGrayText,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "• ${listing.datePosted}",
              fontSize = 11.sp,
              color = Color.Gray
            )
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (language == AppLanguage.SINHALA) "ප්‍රමාණය" else "Quantity",
                fontSize = 10.sp,
                color = Color.Gray
              )
              Text(
                text = listing.quantity,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkGrayText
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = if (language == AppLanguage.SINHALA) "මිල (1Kg)" else "Price / Kg",
                fontSize = 10.sp,
                color = Color.Gray
              )
              Text(
                text = "LKR ${listing.pricePerKg}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ForestGreen
              )
            }
          }
        }
      }

      // PROMINENT "MARK AS SOLD / අලෙවි විය" BUTTON
      if (!isSold) {
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onMarkAsSoldClicked,
            colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            modifier = Modifier.testTag("mark_as_sold_button_${listing.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircleOutline,
                contentDescription = null,
                tint = DarkGrayText,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = if (language == AppLanguage.SINHALA) "අලෙවි විය (Mark as Sold)" else "Mark as Sold",
                color = DarkGrayText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }
  }
}

// BUYERS MARKETPLACE SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyersMarketplaceScreen(
  language: AppLanguage,
  listings: List<CropListing>,
  onNavigateToAddCrop: () -> Unit
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedDistrictFilter by remember { mutableStateOf("All Districts") }
  var selectedInquiryListing by remember { mutableStateOf<CropListing?>(null) }
  var showSuccessOrderDialog by remember { mutableStateOf<Pair<CropListing, String>?>(null) }

  val districts = listOf(
    "All Districts", "Dambulla", "Thambuttegama", "Nuwara Eliya", "Jaffna", "Badulla", "Kurunegala"
  )

  val activeListings = listings.filter { !it.isSold }

  val filteredListings = activeListings.filter { listing ->
    val matchesSearch = searchQuery.isBlank() ||
      listing.cropNameEn.contains(searchQuery, ignoreCase = true) ||
      listing.cropNameSi.contains(searchQuery, ignoreCase = true) ||
      listing.districtEn.contains(searchQuery, ignoreCase = true) ||
      listing.districtSi.contains(searchQuery, ignoreCase = true)

    val matchesDistrict = selectedDistrictFilter == "All Districts" ||
      listing.districtEn.equals(selectedDistrictFilter, ignoreCase = true)

    matchesSearch && matchesDistrict
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Hero Banner for Buyers
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkForestGreen),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = GoldenYellow,
              modifier = Modifier.size(44.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Storefront,
                  contentDescription = null,
                  tint = DarkGrayText,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (language == AppLanguage.SINHALA) "ගැනුම්කරුවන් සඳහා අස්වැන්න වෙළඳපොළ" else "Buyers' Wholesale Marketplace",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.White
              )
              Text(
                text = if (language == AppLanguage.SINHALA) "ශ්‍රී ලංකාවේ ගොවීන්ගෙන් සෘජුවම අස්වැන්න මිලදී ගන්න" else "Buy fresh produce directly from Sri Lankan farmers",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f)
              )
            }
          }

          Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = GoldenYellow,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (language == AppLanguage.SINHALA) "තහවුරු කළ ගොවීන් • සෘජු අලෙවිය" else "Direct Farmer Connect • Wholesale Rates",
                fontSize = 11.sp,
                color = GoldenYellow,
                fontWeight = FontWeight.SemiBold
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = GoldenYellow,
              modifier = Modifier.clickable { onNavigateToAddCrop() }
            ) {
              Text(
                text = if (language == AppLanguage.SINHALA) "+ අස්වැන්නක් පළ කරන්න" else "+ Sell Harvest",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = DarkGrayText,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // Search and District Filter Controls
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = if (language == AppLanguage.SINHALA) "බෝගයේ නම හෝ දිස්ත්‍රික්කය සූයන්න (උදා: තක්කාලි)" else "Search crop or district (e.g. Tomato, Dambulla)",
              fontSize = 13.sp
            )
          },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ForestGreen)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
              }
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("buyer_search_field")
        )

        // District Horizontal Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(districts) { district ->
            val isSelected = selectedDistrictFilter == district
            FilterChip(
              selected = isSelected,
              onClick = { selectedDistrictFilter = district },
              label = {
                val labelText = if (district == "All Districts") {
                  if (language == AppLanguage.SINHALA) "සියලුම දිස්ත්‍රික්ක" else "All Districts"
                } else {
                  val found = SRI_LANKA_DISTRICTS.find { it.nameEn.equals(district, ignoreCase = true) }
                  if (language == AppLanguage.SINHALA && found != null) found.nameSi else district
                }
                Text(
                  text = labelText,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ForestGreen,
                selectedLabelColor = Color.White,
                containerColor = Color.White,
                labelColor = DarkGrayText
              ),
              border = BorderStroke(1.dp, if (isSelected) ForestGreen else Color.LightGray)
            )
          }
        }
      }
    }

    // Results Summary Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.SINHALA)
            "මිලදී ගැනීමට පවතින අස්වැන්න (${filteredListings.size})"
          else
            "Available Crops for Buyers (${filteredListings.size})",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = DarkGrayText
        )

        if (searchQuery.isNotEmpty() || selectedDistrictFilter != "All Districts") {
          TextButton(onClick = {
            searchQuery = ""
            selectedDistrictFilter = "All Districts"
          }) {
            Text(
              text = if (language == AppLanguage.SINHALA) "සියල්ල පෙන්වන්න" else "Reset Filters",
              fontSize = 12.sp,
              color = ForestGreen
            )
          }
        }
      }
    }

    // Buyer Crop Cards List
    if (filteredListings.isEmpty()) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = Color.White),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
        ) {
          Column(
            modifier = Modifier
              .padding(32.dp)
              .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(text = "🔍", fontSize = 44.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (language == AppLanguage.SINHALA) "සෙවුමට ගැළපෙන අස්වැන්නක් හමු නොවීය" else "No matching crops found",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (language == AppLanguage.SINHALA) "වෙනත් බෝගයක නමක් හෝ දිස්ත්‍රික්කයක් සූයන්න" else "Try searching for a different crop name or district",
              fontSize = 12.sp,
              color = MediumGrayText,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      items(filteredListings, key = { it.id }) { listing ->
        BuyerCropCard(
          language = language,
          listing = listing,
          onCallFarmer = {
            Toast.makeText(
              context,
              if (language == AppLanguage.SINHALA) "ගොවි මහතා අමතමින්: ${listing.farmerName} (${listing.farmerPhone})" else "Calling Farmer: ${listing.farmerName} (${listing.farmerPhone})",
              Toast.LENGTH_LONG
            ).show()
          },
          onWhatsAppFarmer = {
            val crop = if (language == AppLanguage.SINHALA) listing.cropNameSi else listing.cropNameEn
            val msg = if (language == AppLanguage.SINHALA)
              "ආයුබෝවන්, මම '${crop}' (${listing.quantity}) මිලදී ගැනීමට කැමතියි."
            else
              "Hello, I am interested in buying '${crop}' (${listing.quantity}) listed on AgriMarket LK."

            Toast.makeText(
              context,
              if (language == AppLanguage.SINHALA) "WhatsApp පණිවිඩය යවන ලදී: ${listing.farmerPhone}" else "WhatsApp Message sent to: ${listing.farmerPhone}\n\"$msg\"",
              Toast.LENGTH_LONG
            ).show()
          },
          onInquireOrder = {
            selectedInquiryListing = listing
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Inquiry Modal Dialog
  selectedInquiryListing?.let { listing ->
    BuyerInquiryDialog(
      language = language,
      listing = listing,
      onDismiss = { selectedInquiryListing = null },
      onSendInquiry = { buyerName, buyerPhone, requestedQty, deliveryPref ->
        selectedInquiryListing = null
        val orderId = "ORD-${UUID.randomUUID().toString().take(6).uppercase()}"
        showSuccessOrderDialog = Pair(listing, orderId)
      }
    )
  }

  // Order Success Dialog
  showSuccessOrderDialog?.let { (listing, orderId) ->
    AlertDialog(
      onDismissRequest = { showSuccessOrderDialog = null },
      icon = {
        Surface(
          shape = CircleShape,
          color = GoldenYellow,
          modifier = Modifier.size(60.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = DarkForestGreen,
              modifier = Modifier.size(36.dp)
            )
          }
        }
      },
      title = {
        Text(
          text = if (language == AppLanguage.SINHALA) "ඇණවුම් විමසීම යොමු විය!" else "Inquiry Sent to Farmer!",
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      },
      text = {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = if (language == AppLanguage.SINHALA)
              "ඔබගේ මිලදී ගැනීමේ විමසීම (අංක: $orderId) ${listing.farmerName} වෙත සාර්ථකව යොමු විය."
            else
              "Your buying inquiry (Ref: $orderId) was sent to ${listing.farmerName}.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
          )

          Surface(
            color = LightForestGreen,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (language == AppLanguage.SINHALA) "ගොවි මහතාගේ දුරකථන අංකය:" else "Farmer Direct Phone:",
                fontSize = 11.sp,
                color = DarkForestGreen
              )
              Text(
                text = listing.farmerPhone,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ForestGreen
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { showSuccessOrderDialog = null },
          colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = if (language == AppLanguage.SINHALA) "හරි (Done)" else "Done",
            fontWeight = FontWeight.Bold
          )
        }
      },
      containerColor = Color.White,
      shape = RoundedCornerShape(20.dp)
    )
  }
}

@Composable
fun BuyerCropCard(
  language: AppLanguage,
  listing: CropListing,
  onCallFarmer: () -> Unit,
  onWhatsAppFarmer: () -> Unit,
  onInquireOrder: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("buyer_crop_card_${listing.id}")
  ) {
    Column {
      // Farmer Info Header
      Surface(
        color = Color(0xFFF2F7F2),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = ForestGreen,
              modifier = Modifier.size(28.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Column {
              Text(
                text = listing.farmerName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = DarkGrayText
              )
              Text(
                text = listing.farmerLocation,
                fontSize = 10.sp,
                color = MediumGrayText
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = LightForestGreen
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = if (language == AppLanguage.SINHALA) "තහවුරු කළ ගොවි" else "Verified",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = DarkForestGreen
              )
            }
          }
        }
      }

      // Crop Main Content
      Row(
        modifier = Modifier.padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Thumbnail Image or Emoji Badge
        if (listing.imageResId != null) {
          Box(
            modifier = Modifier
              .size(90.dp)
              .clip(RoundedCornerShape(12.dp))
          ) {
            Image(
              painter = painterResource(id = listing.imageResId),
              contentDescription = null,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
        } else {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = LightForestGreen,
            modifier = Modifier.size(90.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = listing.iconEmoji, fontSize = 42.sp)
            }
          }
        }

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (language == AppLanguage.SINHALA) listing.cropNameSi else listing.cropNameEn,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = DarkGrayText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Text(
              text = listing.datePosted,
              fontSize = 11.sp,
              color = Color.Gray
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = if (language == AppLanguage.SINHALA) listing.districtSi else listing.districtEn,
              fontSize = 13.sp,
              color = MediumGrayText,
              fontWeight = FontWeight.Medium
            )
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (language == AppLanguage.SINHALA) "පවතින ප්‍රමාණය" else "Available Qty",
                fontSize = 10.sp,
                color = Color.Gray
              )
              Text(
                text = listing.quantity,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkGrayText
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = if (language == AppLanguage.SINHALA) "කිලෝවක මිල" else "Price / Kg",
                fontSize = 10.sp,
                color = Color.Gray
              )
              Text(
                text = "LKR ${listing.pricePerKg}",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = ForestGreen
              )
            }
          }
        }
      }

      Divider(color = Color.LightGray.copy(alpha = 0.4f))

      // Buyer Action Buttons Row (Call, WhatsApp, Inquire)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Call Button
        OutlinedButton(
          onClick = onCallFarmer,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("buyer_call_button_${listing.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call",
            tint = ForestGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (language == AppLanguage.SINHALA) "අමතන්න" else "Call",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
          )
        }

        // WhatsApp Button
        OutlinedButton(
          onClick = onWhatsAppFarmer,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkForestGreen),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1.2f)
            .testTag("buyer_whatsapp_button_${listing.id}")
        ) {
          Text(text = "💬", fontSize = 14.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "WhatsApp",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DarkForestGreen
          )
        }

        // Send Inquiry Button
        Button(
          onClick = onInquireOrder,
          colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1.8f)
            .testTag("buyer_inquire_button_${listing.id}")
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = "Inquire",
            tint = DarkGrayText,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (language == AppLanguage.SINHALA) "ඇණවුම් කරන්න" else "Inquire",
            color = DarkGrayText,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerInquiryDialog(
  language: AppLanguage,
  listing: CropListing,
  onDismiss: () -> Unit,
  onSendInquiry: (name: String, phone: String, qty: String, deliveryPref: String) -> Unit
) {
  var buyerName by remember { mutableStateOf("") }
  var buyerPhone by remember { mutableStateOf("") }
  var requestedQty by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Surface(
        shape = CircleShape,
        color = LightForestGreen,
        modifier = Modifier.size(54.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            tint = ForestGreen,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    },
    title = {
      val crop = if (language == AppLanguage.SINHALA) listing.cropNameSi else listing.cropNameEn
      Text(
        text = if (language == AppLanguage.SINHALA) "$crop මිලදී ගැනීමට ඇණවුම් විමසීම" else "Inquire to Buy $crop",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          color = LightGold,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "ගොවි මහතා: ${listing.farmerName}",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = DarkGrayText
              )
              Text(
                text = "ස්ථානය: ${if (language == AppLanguage.SINHALA) listing.districtSi else listing.districtEn}",
                fontSize = 11.sp,
                color = MediumGrayText
              )
            }
            Text(
              text = "LKR ${listing.pricePerKg} / kg",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = ForestGreen
            )
          }
        }

        OutlinedTextField(
          value = buyerName,
          onValueChange = { buyerName = it },
          label = { Text(if (language == AppLanguage.SINHALA) "ඔබේ නම (Buyer Name)" else "Your Name / Business") },
          placeholder = { Text("e.g., Kandy Wholesale / Perera") },
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
          shape = RoundedCornerShape(10.dp),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = buyerPhone,
          onValueChange = { buyerPhone = it },
          label = { Text(if (language == AppLanguage.SINHALA) "දුරකථන අංකය (Phone)" else "Phone Number") },
          placeholder = { Text("e.g., 077 123 4567") },
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
          shape = RoundedCornerShape(10.dp),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = requestedQty,
          onValueChange = { requestedQty = it },
          label = { Text(if (language == AppLanguage.SINHALA) "අවශ්‍ය ප්‍රමාණය (Required Qty)" else "Required Quantity (Kg)") },
          placeholder = { Text("e.g., 200 Kg (Max: ${listing.quantity})") },
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
          shape = RoundedCornerShape(10.dp),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (buyerName.isBlank() || buyerPhone.isBlank()) {
            return@Button
          }
          onSendInquiry(buyerName, buyerPhone, requestedQty.ifBlank { listing.quantity }, "Direct Pickup")
        },
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = if (language == AppLanguage.SINHALA) "ඇණවුම් විමසීම යවන්න" else "Send Order Inquiry",
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
        Text(text = if (language == AppLanguage.SINHALA) "අවලංගු කරන්න" else "Cancel")
      }
    },
    containerColor = Color.White,
    shape = RoundedCornerShape(20.dp)
  )
}

