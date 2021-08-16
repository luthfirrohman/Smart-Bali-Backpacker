package com.smart.smartbalibackpaker.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.smart.smartbalibackpaker.data.TourismRepository
import com.smart.smartbalibackpaker.data.source.local.entity.TourismDataEntity
import com.smart.smartbalibackpaker.vo.Resource

class PlaceViewModel(private val tourismRepository: TourismRepository) : ViewModel() {

    fun getPlaces(placeType: String): LiveData<Resource<PagedList<TourismDataEntity>>> =
        tourismRepository.getAllTourism(placeType)
}