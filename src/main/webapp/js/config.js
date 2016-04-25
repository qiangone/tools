var envConfig = {
		dummy: false,
		prefix: "http://10.61.213.114:8083/UniversityTool",
		apis: {
			'getCurrentSbu': {
				url: '/getUserInfo',
				dummyPath: ''
			},
			'getProgramList': {
				url: '/api/sbu/courseList',
				dummyPath: ''
			},
			'getAllEvent': {
				url: '/api/course/getAllEvent'
			},
			'getProgramListByEvent': {
				url: '/api/course/getCourseListByEvent'
			},
			'getAllInfo': {
				url: '/api/sbu/countSeats',
				dummyPath: ''
			},
			'assignSeatsForCountry': {
				url: '/api/course/assignSeats',
				dummyPath: ''
			},
			'getOperationList': {
				url: '/api/sbu/getSwapHistory',
				dummyPath: ''
			},
			'searchNominee': {
				url: '/api/participant/query',
				dummyPath: ''
			},
			'addNomineeForCountry': {
				url: '/api/participant/relateParticipant',
				dummyPath: ''
			},
			'removeNomineeForCountry': {
				url: '/api/participant/removeParticipant',
				dummyPath: ''
			},
			'swapSeatsToSbu': {
				url: '/api/sbu/swap',
				dummyPath: ''
			}
		}
};
