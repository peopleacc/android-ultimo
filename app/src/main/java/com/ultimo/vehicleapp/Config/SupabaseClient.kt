package com.ultimo.vehicleapp.Config

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

val supabase: SupabaseClient = createSupabaseClient(
    supabaseUrl = "https://oxgrflidxawowddfusfv.supabase.co",
    supabaseKey = "sb_publishable_r-363Khb-5Dpfjr7Jnmwdw_PEF51hSH"
) {
    install(Postgrest)
    install(Realtime)
    install(Storage)
}
