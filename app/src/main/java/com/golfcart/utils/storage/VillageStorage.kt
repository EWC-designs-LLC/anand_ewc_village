package com.golfcart.utils.storage

import com.golfcart.model.home.CategoryResponse

object VillageStorage {

   private var homeResponse : CategoryResponse ? = null

   fun storeHomeCategoryResponse(homeResponse : CategoryResponse){
      this.homeResponse=homeResponse
   }

   fun getStoreHomeResponse(): CategoryResponse? {
      return homeResponse
   }

}