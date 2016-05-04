var envConfig = {
		dummy: false,
		prefix: "http://10.61.213.168:8080/UniversityTool",
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
			},
			'getSbuList': {
				url: '/api/sbu/sbuList',
				dummyPath: ''
			},
			'deleteSbu': {
				url:'/api/sbu/deleteSbu'
			},
			'uploadPrograms': {
				url: '/api/course/upload'
			},
			'updateProgram': {
				url: '/api/course/updateCourse'
			},
			'getAdminProgramList': {
				url: '/api/course/adminCourseList'
			},
			'getProgramDetail': {
				url: '/api/course/getCourseInfo'
			},
			'updateSwapSeats': {
				url: '/api/sbu/updateSwapSeats'
			},
			'addSbu': {
				url: '/api/sbu/addSbu'
			}
		}
};
