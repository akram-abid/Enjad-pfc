package com.example.depannage.PostPhoneNumber

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RetrivePhoneNumber() {
    RetrivePhoneNumberElements()
}

@Composable
fun RetrivePhoneNumberElements() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val wilayaDairaPairs = listOf(
        Pair("Adrar", "Adrar"),
        Pair("Adrar", "Reggane"),
        Pair("Adrar", "Aoulef"),
        Pair("Adrar", "Timimoun"),
        Pair("Adrar", "Zaouiet Kounta"),
        Pair("Chlef", "Chlef"),
        Pair("Chlef", "Tenes"),
        Pair("Chlef", "Oued Fodda"),
        Pair("Chlef", "Ouled Farès"),
        Pair("Chlef", "El Karimia"),
        Pair("Laghouat", "Laghouat"),
        Pair("Laghouat", "Aflou"),
        Pair("Laghouat", "Brida"),
        Pair("Laghouat", "Ksar El Hirane"),
        Pair("Laghouat", "Sidi Makhlouf"),
        Pair("Oum El Bouaghi", "Oum El Bouaghi"),
        Pair("Oum El Bouaghi", "Ain Beida"),
        Pair("Oum El Bouaghi", "Ain M'lila"),
        Pair("Oum El Bouaghi", "Fkirina"),
        Pair("Oum El Bouaghi", "Sigus"),
        Pair("Batna", "Batna"),
        Pair("Batna", "Barika"),
        Pair("Batna", "Merouana"),
        Pair("Batna", "N'Gaous"),
        Pair("Batna", "Timgad"),
        Pair("Bejaia", "Bejaia"),
        Pair("Bejaia", "Akbou"),
        Pair("Bejaia", "Amizour"),
        Pair("Bejaia", "Sidi Aich"),
        Pair("Bejaia", "Tichy"),
        Pair("Biskra", "Biskra"),
        Pair("Biskra", "El-Kantara"),
        Pair("Biskra", "Ouled Djellal"),
        Pair("Biskra", "Tolga"),
        Pair("Biskra", "Zeribet El Oued"),
        Pair("Blida", "Blida"),
        Pair("Blida", "Boufarik"),
        Pair("Blida", "Bouinan"),
        Pair("Blida", "El Affroun"),
        Pair("Blida", "Mouzaia"),
        Pair("Bouira", "Bouira"),
        Pair("Bouira", "Sour El Ghozlane"),
        Pair("Bouira", "Lakhdaria"),
        Pair("Bouira", "Bir Ghbalou"),
        Pair("Bouira", "El Hachimia"),
        Pair("Tamanrasset", "Tamanrasset"),
        Pair("Tamanrasset", "In Salah"),
        Pair("Tamanrasset", "In Guezzam"),
        Pair("Tamanrasset", "Tazrouk"),
        Pair("Tamanrasset", "Abalessa"),
        Pair("Tebessa", "Tebessa"),
        Pair("Tebessa", "Bir El Ater"),
        Pair("Tebessa", "Cheria"),
        Pair("Tebessa", "El Kouif"),
        Pair("Tebessa", "El Aouinet"),
        Pair("Tlemcen", "Tlemcen"),
        Pair("Tlemcen", "Maghnia"),
        Pair("Tlemcen", "Ghazaouet"),
        Pair("Tlemcen", "Remchi"),
        Pair("Tlemcen", "Sebdou"),
        Pair("Algiers", "Bab El Oued"),
        Pair("Algiers", "El Harrach"),
        Pair("Algiers", "Rouiba"),
        Pair("Algiers", "Baraki"),
        Pair("Algiers", "Bir Mourad Raïs"),
        Pair("Oran", "Oran"),
        Pair("Oran", "Es Senia"),
        Pair("Oran", "Arzew"),
        Pair("Oran", "Gdyel"),
        Pair("Oran", "Bir El Djir"),
        Pair("Constantine", "Constantine"),
        Pair("Constantine", "Hamma Bouziane"),
        Pair("Constantine", "El Khroub"),
        Pair("Constantine", "Zighoud Youcef"),
        Pair("Constantine", "Ain Abid"),
        Pair("Setif", "Setif"),
        Pair("Setif", "El Eulma"),
        Pair("Setif", "Ain Arnat"),
        Pair("Setif", "Ain Azel"),
        Pair("Setif", "Bougaa"),
        Pair("Sidi Bel Abbes", "Sidi Bel Abbes"),
        Pair("Sidi Bel Abbes", "Telagh"),
        Pair("Sidi Bel Abbes", "Ben Badis"),
        Pair("Sidi Bel Abbes", "Tessala"),
        Pair("Sidi Bel Abbes", "Merine"),
        Pair("Annaba", "Annaba"),
        Pair("Annaba", "El Hadjar"),
        Pair("Annaba", "Berrahal"),
        Pair("Annaba", "Ain Berda"),
        Pair("Annaba", "El Bouni"),
        Pair("Guelma", "Guelma"),
        Pair("Guelma", "Bouchegouf"),
        Pair("Guelma", "Héliopolis"),
        Pair("Guelma", "Oued Zenati"),
        Pair("Guelma", "Ain Makhlouf"),
        Pair("Tizi Ouzou", "Tizi Ouzou"),
        Pair("Tizi Ouzou", "Azazga"),
        Pair("Tizi Ouzou", "Larbaa Nath Irathen"),
        Pair("Tizi Ouzou", "Boghni"),
        Pair("Tizi Ouzou", "Draâ Ben Khedda"),
        Pair("Béchar", "Béchar"),
        Pair("Béchar", "Kenadsa"),
        Pair("Béchar", "Abadla"),
        Pair("Béchar", "Taghit"),
        Pair("Béchar", "Lahmar"),
        Pair("Tiaret", "Tiaret"),
        Pair("Tiaret", "Sougueur"),
        Pair("Tiaret", "Medroussa"),
        Pair("Tiaret", "Mahdia"),
        Pair("Tiaret", "Frenda"),
        Pair("Bordj Bou Arreridj", "Bordj Bou Arreridj"),
        Pair("Bordj Bou Arreridj", "Ras El Oued"),
        Pair("Bordj Bou Arreridj", "Bir Kasdali"),
        Pair("Bordj Bou Arreridj", "El Achir"),
        Pair("Bordj Bou Arreridj", "Mansoura"),
        Pair("Boumerdes", "Boumerdes"),
        Pair("Boumerdes", "Boudouaou"),
        Pair("Boumerdes", "Thenia"),
        Pair("Boumerdes", "Dellys"),
        Pair("Boumerdes", "Naciria"),
        Pair("El Tarf", "El Tarf"),
        Pair("El Tarf", "Bouteldja"),
        Pair("El Tarf", "Ben M'Hidi"),
        Pair("El Tarf", "Bouhadjar"),
        Pair("El Tarf", "Dréan"),
        Pair("Tindouf", "Tindouf"),
        Pair("Tindouf", "Oum el Assel"),
        Pair("Tissemsilt", "Tissemsilt"),
        Pair("Tissemsilt", "Theniet El Had"),
        Pair("Tissemsilt", "Khemisti"),
        Pair("Tissemsilt", "Bordj Emir Abdelkader"),
        Pair("Tissemsilt", "Lazharia"),
        Pair("El Oued", "El Oued"),
        Pair("El Oued", "Robbah"),
        Pair("El Oued", "Magrane"),
        Pair("El Oued", "Guemar"),
        Pair("El Oued", "Debila"),
        Pair("Khenchela", "Khenchela"),
        Pair("Khenchela", "Kais"),
        Pair("Khenchela", "Bouhmama"),
        Pair("Khenchela", "El Hamma"),
        Pair("Khenchela", "Chelia"),
        Pair("Souk Ahras", "Souk Ahras"),
        Pair("Souk Ahras", "Sidi Fredj"),
        Pair("Souk Ahras", "Sedrata"),
        Pair("Souk Ahras", "Hanancha"),
        Pair("Souk Ahras", "Taoura"),
        Pair("Tipaza", "Tipaza"),
        Pair("Tipaza", "Cherchell"),
        Pair("Tipaza", "Fouka"),
        Pair("Tipaza", "Bou Ismail"),
        Pair("Tipaza", "Kolea"),
        Pair("Mila", "Mila"),
        Pair("Mila", "Tadjenanet"),
        Pair("Mila", "Chelghoum Laïd"),
        Pair("Mila", "Grarem Gouga"),
        Pair("Mila", "Aïn Beida Harriche"),
        Pair("Aïn Defla", "Aïn Defla"),
        Pair("Aïn Defla", "El Attaf"),
        Pair("Aïn Defla", "Djelida"),
        Pair("Aïn Defla", "El Abadia"),
        Pair("Naâma", "Naâma"),
        Pair("Naâma", "Ain Sefra"),
        Pair("Naâma", "Mekmen Ben Amar"),
        Pair("Naâma", "Tiout"),
        Pair("Naâma", "Asla"),
        Pair("Naâma", "Sfissifa"),
        Pair("Naâma", "Kasdir"),
        Pair("Tizi Ouzou", "Ouadhias"),
        Pair("Tizi Ouzou", "Iferhounène"),
        Pair("Tizi Ouzou", "Beni Douala"),
        Pair("Tizi Ouzou", "Mekla"),
        Pair("Tizi Ouzou", "Freha"),
        Pair("Tizi Ouzou", "Azeffoun"),
        Pair("Djelfa", "Djelfa"),
        Pair("Djelfa", "Messaad"),
        Pair("Djelfa", "Hassi Bahbah"),
        Pair("Djelfa", "Ain Oussera"),
        Pair("Djelfa", "Dar Chioukh"),
        Pair("Jijel", "Jijel"),
        Pair("Jijel", "El Milia"),
        Pair("Jijel", "Taher"),
        Pair("Jijel", "Chekfa"),
        Pair("Jijel", "El Aouana"),
        Pair("Saïda", "Saïda"),
        Pair("Saïda", "Youb"),
        Pair("Saïda", "Aïn El Hadjar"),
        Pair("Saïda", "Hounet"),
        Pair("Saïda", "Sidi Boubkeur"),
        Pair("Skikda", "Skikda"),
        Pair("Skikda", "El Harrouch"),
        Pair("Skikda", "Azzaba"),
        Pair("Skikda", "Collo"),
        Pair("Skikda", "Tamalous"),
        Pair("Mostaganem", "Mostaganem"),
        Pair("Mostaganem", "Aïn Nouïssy"),
        Pair("Mostaganem", "Sidi Ali"),
        Pair("Mostaganem", "Hassi Mamèche"),
        Pair("Mostaganem", "Achaacha"),
        Pair("Msila", "Msila"),
        Pair("Msila", "Bou Saada"),
        Pair("Msila", "Ain El Hadjel"),
        Pair("Msila", "Sidi Aissa"),
        Pair("Msila", "Belaiba"),
        Pair("Relizane", "Relizane"),
        Pair("Relizane", "Oued Rhiou"),
        Pair("Relizane", "Yellel"),
        Pair("Relizane", "Mazouna"),
        Pair("Relizane", "Ammi Moussa")
    )

    val wilaya = wilayaDairaPairs.map { it.first }.toSet()

    // State for selected items
    var selectedItemOne by remember { mutableStateOf(wilaya.first()) }
    var selectedItemTwo by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isExpandedWilaya by remember { mutableStateOf(false) }
    var isExpandedDaira by remember { mutableStateOf(false) }
    val posts = remember { mutableStateListOf<Post>() }

    // Filter daira based on selected wilaya
    val daira = wilayaDairaPairs.filter { it.first == selectedItemOne }.map { it.second }

    // Function to fetch posts from Firestore
    fun fetchPosts() {
        db.collection("phone Numbers").get().addOnSuccessListener { documents ->
            posts.clear()
            for (document in documents) {
                if (document.get("wilaya").toString() == selectedItemOne &&
                    document.get("daira").toString() == selectedItemTwo
                    ) {
                    posts.add(
                        Post(
                            document.get("phone number").toString(),
                            document.get("name").toString(),
                            document.get("wilaya").toString(),
                            document.get("daira").toString(),
                            document.get("Name").toString()
                        )
                    )
                }
            }
        }
    }

    // Fetch posts on initial composition
    LaunchedEffect(Unit) {
        fetchPosts()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Column {
                    Text(
                        text = "Post your phone number",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = "You can post your phone number after you choose the wilaya and the daira you want.",
                        modifier = Modifier.padding(12.dp, 4.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Wilaya", Modifier.padding(4.dp))
                ExposedDropdownMenuBox(
                    expanded = isExpandedWilaya,
                    onExpandedChange = { isExpandedWilaya = !isExpandedWilaya },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp),
                        value = selectedItemOne,
                        onValueChange = {},
                        shape = RoundedCornerShape(18.dp),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedWilaya) }
                    )
                    ExposedDropdownMenu(
                        expanded = isExpandedWilaya,
                        onDismissRequest = { isExpandedWilaya = false }) {
                        wilaya.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    selectedItemOne = item
                                    selectedItemTwo = ""
                                    isExpandedWilaya = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Daira", Modifier.padding(4.dp))
                ExposedDropdownMenuBox(
                    expanded = isExpandedDaira,
                    onExpandedChange = { isExpandedDaira = !isExpandedDaira },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp),
                        value = selectedItemTwo,
                        onValueChange = {},
                        shape = RoundedCornerShape(18.dp),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedDaira) }
                    )
                    ExposedDropdownMenu(
                        expanded = isExpandedDaira,
                        onDismissRequest = { isExpandedDaira = false }) {
                        daira.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    selectedItemTwo = item
                                    isExpandedDaira = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
                        fetchPosts()
                },
                Modifier.padding(12.dp)
            ) {
                Text(text = "retrive phone numbers", color = MaterialTheme.colorScheme.scrim)
            }
            Divider(thickness = 2.dp, modifier = Modifier.padding(18.dp))
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    Surface (
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(6.dp)
                    ){

                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Phone Number: ${post.phoneNumber}", Modifier.padding(4.dp))
                            Text(text = "Wilaya: ${post.wilaya}", Modifier.padding(4.dp))
                            Text(text = "Daira: ${post.daira}", Modifier.padding(4.dp))
                            Text(text = "name: ${post.worker}", Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}

