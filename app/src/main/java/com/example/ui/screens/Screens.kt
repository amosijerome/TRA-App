package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Receipt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.R
import com.example.data.model.*
import com.example.ui.ChatMessage
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// Formatter for currency
val currencyFormat = DecimalFormat("#,###.##")

// ----------------------------------------------------
// 1. SPLASH SCREEN
// ----------------------------------------------------
@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = startAnimation,
            enter = fadeIn(animationSpec = tween(1000)) + expandVertically(animationSpec = tween(1000)),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Customized TRA logo using Canvas
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFFF59E0B), CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1E3A8A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "TRA Emblem",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "TRA COLLECTOR",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Revenue Collector Assistant",
                    color = Color(0xFF94A3B8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                CircularProgressIndicator(
                    color = Color(0xFFF59E0B),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------
// 2. LOGIN SCREEN
// ----------------------------------------------------
@Composable
fun LoginScreen(navController: NavController, viewModel: MainViewModel) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_tra_banner),
                contentDescription = "TRA Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x99000000)),
                            startY = 100f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Tanzania Revenue Authority",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TRA Revenue Collector Assistant",
                    color = Color(0xFFFBBF24),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "Sign in to access your dashboard",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 24.dp)
            )

            // Input Fields
            TextField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                label = { Text("Email or Phone Number") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("username_input"),
                shape = RoundedCornerShape(12.dp)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            authError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (emailOrPhone.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.login(emailOrPhone.trim(), password.trim()) {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text("Don't have an account? Register here", color = MaterialTheme.colorScheme.primary)
            }

            // Quick Access Section (Prepopulated roles for easy demonstration)
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Quick Access demo profiles",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        emailOrPhone = "officer@tra.go.tz"
                        password = "password123"
                    },
                    modifier = Modifier.weight(1f).padding(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Officer", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = {
                        emailOrPhone = "abasi@gmail.com"
                        password = "password123"
                    },
                    modifier = Modifier.weight(1f).padding(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Taxpayer", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = {
                        emailOrPhone = "admin@tra.go.tz"
                        password = "admin123"
                    },
                    modifier = Modifier.weight(1f).padding(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Admin", fontSize = 11.sp)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. REGISTRATION SCREEN
// ----------------------------------------------------
@Composable
fun RegisterScreen(navController: NavController, viewModel: MainViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Revenue Officer") }
    var isExpanded by remember { mutableStateOf(false) }

    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val rolesList = listOf("Revenue Officer", "Taxpayer", "Administrator")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Register as a TRA Officer or Taxpayer",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        // Custom Exposed Dropdown Menu Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            OutlinedButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Account Role: $selectedRole",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                rolesList.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            isExpanded = false
                        }
                    )
                }
            }
        }

        authError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.register(fullName.trim(), email.trim(), phone.trim(), password.trim(), selectedRole) {
                    navController.navigate("dashboard") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_register"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Register Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ----------------------------------------------------
// 4. MAIN NAVIGATION & DASHBOARD SCREEN
// ----------------------------------------------------
@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingPaymentsCount.collectAsStateWithLifecycle()
    val taxpayersList by viewModel.taxpayers.collectAsStateWithLifecycle()
    val paymentsList by viewModel.payments.collectAsStateWithLifecycle()

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (currentUser == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("dashboard") { inclusive = true }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TRA PORTAL",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = currentUser?.fullName ?: "Guest User",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Box {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            if (pendingCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            TRA_BottomBar(navController, "dashboard")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Welcome Card with role badge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRA Assistant Dashboard",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFBBF24), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentUser?.role ?: "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Jamhuri ya Muungano wa Tanzania",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Honesty, Professionalism, and Customer Care",
                        fontSize = 12.sp,
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Role-Based Screen Rendering
            when (currentUser?.role) {
                "Revenue Officer" -> OfficerDashboardContent(
                    totalRev = totalRevenue,
                    taxpayersCount = taxpayersList.size,
                    pending = pendingCount,
                    recentPayments = paymentsList.take(5),
                    navController = navController
                )
                "Taxpayer" -> TaxpayerDashboardContent(
                    currentUser = currentUser!!,
                    payments = paymentsList,
                    navController = navController
                )
                else -> AdminDashboardContent(
                    totalRev = totalRevenue,
                    recentPayments = paymentsList,
                    navController = navController
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to log out of the TRA Collector Assistant?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout {
                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ----------------------------------------------------
// BOTTOM BAR NAVIGATION COMPONENT
// ----------------------------------------------------
@Composable
fun TRA_BottomBar(navController: NavController, currentRoute: String) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = {
                if (currentRoute != "dashboard") {
                    navController.navigate("dashboard") { popUpTo(0) }
                }
            },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "taxpayers",
            onClick = {
                if (currentRoute != "taxpayers") {
                    navController.navigate("taxpayers") { popUpTo("dashboard") }
                }
            },
            icon = { Icon(Icons.Default.People, contentDescription = "Taxpayers") },
            label = { Text("Taxpayers", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "payments",
            onClick = {
                if (currentRoute != "payments") {
                    navController.navigate("payments") { popUpTo("dashboard") }
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.Receipt, contentDescription = "Payments") },
            label = { Text("Payments", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "reports",
            onClick = {
                if (currentRoute != "reports") {
                    navController.navigate("reports") { popUpTo("dashboard") }
                }
            },
            icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
            label = { Text("Reports", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "help",
            onClick = {
                if (currentRoute != "help") {
                    navController.navigate("help") { popUpTo("dashboard") }
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Help") },
            label = { Text("AI Help", fontSize = 11.sp) }
        )
    }
}

// ----------------------------------------------------
// OFFICER DASHBOARD CONTENT
// ----------------------------------------------------
@Composable
fun OfficerDashboardContent(
    totalRev: Double,
    taxpayersCount: Int,
    pending: Int,
    recentPayments: List<Payment>,
    navController: NavController
) {
    Text(
        text = "Key Statistics",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    // Stats Grid
    Row(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Total Collected", fontSize = 11.sp, color = Color.Gray)
                Text(
                    text = "${currencyFormat.format(totalRev)} TZS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Default.Group, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Active Taxpayers", fontSize = 11.sp, color = Color.Gray)
                Text(text = "$taxpayersCount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFFF59E0B))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pending Payments", fontSize = 11.sp, color = Color.Gray)
                Text(text = "$pending", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .clickable { navController.navigate("collect_form") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Default.AddCard, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Record Payment", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(text = "Collect +", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Interactive Custom Chart using Canvas (Zero external dependency chart)
    Text(
        text = "Revenue by Tax Type",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mocking data proportions for 4 tax types based on current prepopulated DB
                // VAT: 12.5M, Income: 5.7M, Customs: 8.5M (Pending), Withholding: 3.2M
                val chartData = listOf(
                    "VAT" to 12.5f,
                    "Income" to 5.7f,
                    "Customs" to 8.5f,
                    "Withholding" to 3.2f
                )
                val maxVal = chartData.maxOf { it.second }

                chartData.forEach { (label, value) ->
                    val proportion = value / maxVal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "${value}M", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .fillMaxHeight(proportion * 0.75f)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(0.6f))
                                    ),
                                    RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Recent Payments
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Recent Collections", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TextButton(onClick = { navController.navigate("payments") }) {
            Text("See All")
        }
    }

    recentPayments.forEach { payment ->
        PaymentItemRow(payment = payment, onClick = {})
    }
}

// ----------------------------------------------------
// TAXPAYER DASHBOARD CONTENT
// ----------------------------------------------------
@Composable
fun TaxpayerDashboardContent(
    currentUser: User,
    payments: List<Payment>,
    navController: NavController
) {
    // Standard taxpayers are matched by name in our preseeded demo
    val taxpayerName = currentUser.fullName
    // In progress payments for this specific taxpayer
    val taxpayerPayments = payments.filter { it.taxpayerName.contains(taxpayerName, ignoreCase = true) || it.taxpayerName.contains("Mollel", ignoreCase = true) }

    val totalPaid = taxpayerPayments.filter { it.status == "Completed" }.sumOf { it.amount }
    val totalPending = taxpayerPayments.filter { it.status == "Pending" }.sumOf { it.amount }

    Text(
        text = "Your Tax Profile",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Registered Name: $taxpayerName", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "TIN: 123-456-789 (Demonstration)", color = Color.Gray, fontSize = 13.sp)
            Text(text = "Business Type: Manufacturing / Service", color = Color.Gray, fontSize = 13.sp)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Total Paid (TZS)", fontSize = 11.sp, color = Color.Gray)
                    Text(text = currencyFormat.format(totalPaid), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Pending Obligation", fontSize = 11.sp, color = Color.Gray)
                    Text(text = currencyFormat.format(totalPending), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "My Tax Obligations",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // Checklist of obligations
    val obligations = listOf(
        Triple("Value Added Tax (VAT)", "Monthly filing, due 20th of every month", true),
        Triple("Corporate Income Tax", "Quarterly installment due June 30th", false),
        Triple("Withholding Tax", "Due within 7 days of payment", true)
    )

    obligations.forEach { (title, subtitle, status) ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                Icon(
                    imageVector = if (status) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (status) Color(0xFF10B981) else Color(0xFFF59E0B)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Your Recent Transactions",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    if (taxpayerPayments.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No recent tax payments found.", color = Color.Gray)
        }
    } else {
        taxpayerPayments.forEach { payment ->
            PaymentItemRow(payment = payment, onClick = {})
        }
    }
}

// ----------------------------------------------------
// ADMIN DASHBOARD CONTENT
// ----------------------------------------------------
@Composable
fun AdminDashboardContent(
    totalRev: Double,
    recentPayments: List<Payment>,
    navController: NavController
) {
    Text(
        text = "Administrator Controls",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    // Summary widgets
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Total TRA Collection Pool", fontSize = 12.sp, color = Color.Gray)
            Text(
                text = "${currencyFormat.format(totalRev)} TZS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Across ${recentPayments.size} verified transactions",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    // Quick Admin Actions
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { navController.navigate("reports") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Assessment, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Manage Reports", fontSize = 11.sp)
        }

        Button(
            onClick = { navController.navigate("taxpayers") },
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.People, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Audit Registry", fontSize = 11.sp)
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Live Audit Trail",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    recentPayments.forEach { payment ->
        PaymentItemRow(payment = payment, onClick = {})
    }
}

// ----------------------------------------------------
// PAYMENT ITEM ROW COMPONENT
// ----------------------------------------------------
@Composable
fun PaymentItemRow(payment: Payment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.taxpayerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${payment.taxType} • ${payment.paymentDate}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${currencyFormat.format(payment.amount)} TZS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(
                            color = if (payment.status == "Completed") Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = payment.status,
                        color = if (payment.status == "Completed") Color(0xFF065F46) else Color(0xFF92400E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. TAXPAYER REGISTRY & MANAGEMENT SCREEN
// ----------------------------------------------------
@Composable
fun TaxpayersScreen(navController: NavController, viewModel: MainViewModel) {
    val searchVal by viewModel.searchQuery.collectAsStateWithLifecycle()
    val taxpayersList by viewModel.taxpayers.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    // Add form states
    var tpName by remember { mutableStateOf("") }
    var tpTin by remember { mutableStateOf("") }
    var tpBusiness by remember { mutableStateOf("Retail") }
    var tpAddress by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val businessTypes = listOf("Retail", "Wholesale", "Manufacturing", "Service", "Individual")

    val context = LocalContext.current

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Taxpayer Registry") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            TRA_BottomBar(navController, "taxpayers")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Taxpayer")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Search Bar
            TextField(
                value = searchVal,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search by Name or TIN...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            )

            if (taxpayersList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No taxpayers registered yet.", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = rememberLazyListState()
                ) {
                    items(taxpayersList) { tp ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = tp.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = tp.businessType,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "TIN Number: ${tp.tinNumber}", fontSize = 13.sp, color = Color.Gray)
                                Text(text = "Address: ${tp.address}", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Register New Taxpayer") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    TextField(
                        value = tpName,
                        onValueChange = { tpName = it },
                        label = { Text("Taxpayer / Business Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    TextField(
                        value = tpTin,
                        onValueChange = { tpTin = it },
                        label = { Text("TIN Number (9 Digits)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Dropdown for business type
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        OutlinedButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Type: $tpBusiness")
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            businessTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        tpBusiness = type
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    TextField(
                        value = tpAddress,
                        onValueChange = { tpAddress = it },
                        label = { Text("Physical Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tpName.isBlank() || tpTin.isBlank() || tpAddress.isBlank()) {
                            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.registerTaxpayer(tpName, tpTin, tpBusiness, tpAddress) {
                            Toast.makeText(context, "Taxpayer registered successfully", Toast.LENGTH_SHORT).show()
                            showAddDialog = false
                            // Clear fields
                            tpName = ""
                            tpTin = ""
                            tpAddress = ""
                        }
                    }
                ) {
                    Text("Register")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ----------------------------------------------------
// 6. REVENUE COLLECTION FORM
// ----------------------------------------------------
@Composable
fun CollectFormScreen(navController: NavController, viewModel: MainViewModel) {
    val taxpayersList by viewModel.taxpayers.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedTaxpayer by remember { mutableStateOf<Taxpayer?>(null) }
    var amountText by remember { mutableStateOf("") }
    var selectedTaxType by remember { mutableStateOf("Value Added Tax (VAT)") }
    var isTpExpanded by remember { mutableStateOf(false) }
    var isTypeExpanded by remember { mutableStateOf(false) }

    val taxTypes = listOf("Value Added Tax (VAT)", "Income Tax", "Withholding Tax", "Customs Duty")

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Collect Revenue") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Record Tax Collection",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Select registered taxpayer and record incoming transactions securely.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 1. Taxpayer Dropdown Select
            Text(text = "Taxpayer Selection", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                OutlinedButton(
                    onClick = { isTpExpanded = !isTpExpanded },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTaxpayer?.name ?: "Select registered taxpayer",
                            color = if (selectedTaxpayer == null) Color.Gray else MaterialTheme.colorScheme.onBackground
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = isTpExpanded,
                    onDismissRequest = { isTpExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    if (taxpayersList.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No taxpayers found. Please register one first.") },
                            onClick = {
                                isTpExpanded = false
                                navController.navigate("taxpayers")
                            }
                        )
                    } else {
                        taxpayersList.forEach { tp ->
                            DropdownMenuItem(
                                text = { Text("${tp.name} (${tp.tinNumber})") },
                                onClick = {
                                    selectedTaxpayer = tp
                                    isTpExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 2. Amount Input
            Text(text = "Collection Amount (TZS)", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            TextField(
                value = amountText,
                onValueChange = { amountText = it },
                placeholder = { Text("Enter TZS value") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("payment_amount_input"),
                shape = RoundedCornerShape(12.dp)
            )

            // 3. Tax Type Select
            Text(text = "Tax Category / Type", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                OutlinedButton(
                    onClick = { isTypeExpanded = !isTypeExpanded },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedTaxType, color = MaterialTheme.colorScheme.onBackground)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = isTypeExpanded,
                    onDismissRequest = { isTypeExpanded = false }
                ) {
                    taxTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedTaxType = type
                                isTypeExpanded = false
                            }
                        )
                    }
                }
            }

            // Record Button
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (selectedTaxpayer == null) {
                        Toast.makeText(context, "Please select a taxpayer", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (amount == null || amount <= 0) {
                        Toast.makeText(context, "Please enter a valid positive amount", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val currentDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    viewModel.recordPayment(
                        taxpayerId = selectedTaxpayer!!.id,
                        taxpayerName = selectedTaxpayer!!.name,
                        amount = amount,
                        taxType = selectedTaxType,
                        paymentDate = currentDateStr,
                        status = "Completed"
                    ) {
                        Toast.makeText(context, "Payment collected successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_payment_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm & File Collection", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// 7. PAYMENTS & TRANSACTIONS HISTORY (RECEIPT GENERATOR)
// ----------------------------------------------------
@Composable
fun PaymentsScreen(navController: NavController, viewModel: MainViewModel) {
    val paymentsList by viewModel.payments.collectAsStateWithLifecycle()
    var selectedPaymentForReceipt by remember { mutableStateOf<Payment?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Collection Ledger") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            TRA_BottomBar(navController, "payments")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Transaction Records",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Tap on any transaction row to generate or print verified tax receipt.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (paymentsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions recorded in ledger.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = rememberLazyListState()
                ) {
                    items(paymentsList) { payment ->
                        PaymentItemRow(payment = payment, onClick = {
                            selectedPaymentForReceipt = payment
                        })
                    }
                }
            }
        }
    }

    // Receipt Modal dialog
    selectedPaymentForReceipt?.let { payment ->
        Dialog(onDismissRequest = { selectedPaymentForReceipt = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Logo
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF1E3A8A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TANZANIA REVENUE AUTHORITY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "OFFICIAL TAX RECEIPT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF10B981)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Receipt Info
                    ReceiptRow(label = "Receipt ID", value = "TRA-REC-739${payment.id}")
                    ReceiptRow(label = "Date", value = payment.paymentDate)
                    ReceiptRow(label = "Taxpayer", value = payment.taxpayerName)
                    ReceiptRow(label = "TIN", value = "123-456-789")
                    ReceiptRow(label = "Tax Type", value = payment.taxType)
                    ReceiptRow(label = "Status", value = payment.status)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "TOTAL PAID", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "${currencyFormat.format(payment.amount)} TZS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E3A8A)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stamp placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(BorderStroke(2.dp, Color(0xFF10B981)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TRA PAID\nVERIFIED",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { selectedPaymentForReceipt = null },
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Text("Close")
                        }
                        Button(
                            onClick = {
                                Toast.makeText(context, "Receipt sent to printer/email!", Toast.LENGTH_SHORT).show()
                                selectedPaymentForReceipt = null
                            },
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Black)
    }
}

// ----------------------------------------------------
// 8. REPORTS GENERATION SCREEN
// ----------------------------------------------------
@Composable
fun ReportsScreen(navController: NavController, viewModel: MainViewModel) {
    val reportsList by viewModel.reports.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedReportForView by remember { mutableStateOf<Report?>(null) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Collection Reports") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            TRA_BottomBar(navController, "reports")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Generate New Report",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val officerName = currentUser?.fullName ?: "Unknown Officer"
                        viewModel.generateReport("Daily", officerName)
                        Toast.makeText(context, "Daily collection report generated!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).padding(end = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Daily Report", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val officerName = currentUser?.fullName ?: "Unknown Officer"
                        viewModel.generateReport("Monthly", officerName)
                        Toast.makeText(context, "Monthly collection report generated!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).padding(start = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Monthly Report", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Generated Report History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (reportsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No reports generated yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(reportsList) { report ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedReportForView = report },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${report.reportType} Collection Audit",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "By ${report.generatedBy} • ${report.generatedDate}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }

    // View Report Dialog
    selectedReportForView?.let { report ->
        Dialog(onDismissRequest = { selectedReportForView = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "TRA REPORT DETAILED AUDIT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E3A8A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = report.summaryText,
                        fontSize = 13.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { selectedReportForView = null },
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Text("Close")
                        }
                        Button(
                            onClick = {
                                Toast.makeText(context, "Exporting TRA_${report.reportType}_Report.pdf to Documents...", Toast.LENGTH_LONG).show()
                                selectedReportForView = null
                            },
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF")
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 9. NOTIFICATIONS SCREEN
// ----------------------------------------------------
@Composable
fun NotificationsScreen(navController: NavController, viewModel: MainViewModel) {
    val pendingCount by viewModel.pendingPaymentsCount.collectAsStateWithLifecycle()

    val notificationItems = remember(pendingCount) {
        listOf(
            Triple(
                "VAT Payment Reminder",
                "Value Added Tax (VAT) collection cycle is closing soon. Ensure all merchants have logged invoices.",
                "Due in 2 days"
            ),
            Triple(
                "Pending Revenue Audit",
                "There are currently $pendingCount pending collections awaiting verification.",
                "Action Required"
            ),
            Triple(
                "TRA Policy Update",
                "Please review the revised electronic tax record system guidelines effective from next month.",
                "General Announcement"
            )
        )
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("TRA Reminders & Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            state = rememberLazyListState()
        ) {
            items(notificationItems) { (title, content, tag) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (tag.contains("Action") || tag.contains("2 days")) Color(0xFFFEE2E2) else Color(0xFFE0F2FE),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = if (tag.contains("Action") || tag.contains("2 days")) Color(0xFF991B1B) else Color(0xFF0369A1),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = content, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 10. HELP SCREEN & AI TAX ASSISTANT (GEMINI INTEGRATED)
// ----------------------------------------------------
@Composable
fun HelpScreen(navController: NavController, viewModel: MainViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.chatLoading.collectAsStateWithLifecycle()
    var userQuery by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()

    // Scroll to bottom when a new chat arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("AI Tax Assistant") },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear Chat")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            TRA_BottomBar(navController, "help")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Chat Message Box
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.LightGray.copy(0.3f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("TRA Assistant is thinking...", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // Input area
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = userQuery,
                        onValueChange = { userQuery = it },
                        placeholder = { Text("Ask about VAT, TIN, Income tax rate...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (userQuery.isNotBlank()) {
                                viewModel.sendChatMessage(userQuery.trim())
                                userQuery = ""
                            }
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .testTag("send_chat_button"),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send Message")
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CHAT BUBBLE COMPONENT
// ----------------------------------------------------
@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.35f),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = if (isUser) "You" else "TRA AI Assistant",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isUser) Color.White.copy(0.8f) else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
